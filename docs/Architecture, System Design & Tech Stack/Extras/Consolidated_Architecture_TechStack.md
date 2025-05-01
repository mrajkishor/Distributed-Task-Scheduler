
## ✅ Step 1.1 – Define Architecture, System Design & Tech Stack

---

### 📌 Project Description

The Distributed Task Scheduler is a backend infrastructure system built to handle **background job processing** in a scalable, fault-tolerant, and event-driven manner. It is inspired by job systems like **Celery, Sidekiq, and Quartz**, with enhancements for **delay, retries, DAG (Directed Acyclic Graph) support,** and real-time observability.

The goal is to allow clients to submit tasks (jobs) via REST APIs, which are stored in a job queue, executed asynchronously by distributed workers, and monitored through a fully observable and secure pipeline.

---

### 🧱 System Design

#### 🔹 Key Functional Requirements:
- Accept task/job submission from client
- Queue the job in Redis with optional delay/priority
- Support retries, exponential backoff, and dead letter queue
- Allow defining dependencies (chained or DAG-based jobs)
- Provide job status tracking (e.g., pending, success, failed)
- Expose secure REST APIs with JWT-based access
- Notify clients via email or webhook after execution
- Support horizontal scaling for processing multiple jobs
- Enable real-time monitoring of job metrics
- Ensure fault-tolerant execution of jobs
- Handle worker failures gracefully (retries, leasing)

---

### 🧩 High-Level Architecture Diagram

> ✅ *You can include the image you generated earlier or recreate it in draw.io/Figma.*  
> _Diagram file: `docs/architecture.png` will be included in the final submission and README._

```
                +----------------------+
                |      Client (UI)     |
                +----------+-----------+
                           |
                           v
               +-----------+------------+
               |        API Service     |
               |  (Spring Boot REST)    |
               +-----------+------------+
                           |
                +----------+----------+
                |     PostgreSQL      | <------+
                |  (Job Metadata DB)  |        |
                +----------+----------+        |
                           |                   |
                           v                   |
               +-----------+------------+      |
               |         Redis Queue     | <---+
               |   (delay, priority, DAG)|
               +-----------+------------+
                           |
                +----------v----------+
                |   Worker Pool        |
                | (Task Executors)     |
                +----------+----------+
                           |
                +----------v----------+
                |     Kafka / DLQ     |
                | (Job failures, logs)|
                +----------+----------+
                           |
                +----------v-----------+
                |   Notifier Service    |
                | (Email/Webhook)       |
                +-----------------------+

         +------------------+     +-----------------+
         |   Prometheus     |<--->|     Grafana      |
         +------------------+     +-----------------+

  +------------------+     +---------------------+
  | AWS SSM / YAML   |     | GitHub Actions CI/CD|
  +------------------+     +---------------------+
```

---

### ⚙️ Detailed Component Description

| Component           | Description |
|---------------------|-------------|
| **API Service**     | Exposes REST endpoints to accept new task submissions and query job statuses. Implements authentication using JWT and role-based access control. |
| **Redis Queue**     | Stores tasks using sorted sets and keys to support delay, retries, and priority. Manages task leasing and locking. |
| **PostgreSQL**      | Stores metadata like task ID, type, status, timestamps, error messages, and retry history. |
| **Worker Pool**     | Fetches tasks from Redis, executes the logic, updates status in PostgreSQL. Supports retry, backoff, and DAG resolution. |
| **Kafka / RabbitMQ**| Used for DLQ and event-based job notification. Ensures failed jobs are not lost. |
| **Notifier Service**| Sends webhook and email notifications upon job success or failure using Spring Mail or HTTP clients. |
| **Config Server**   | Loads environment-based configs dynamically using YAML files or AWS SSM Parameter Store. |
| **Prometheus + Grafana** | Collects real-time metrics like task execution time, queue size, retry count, and failure rates. Visualized on dashboards. |
| **CI/CD + Deployment**| GitHub Actions handles build-test-deploy workflows. Services are dockerized and deployed using Kubernetes (Minikube for local, K3s or EC2 optional). |

---

### 🧰 Tech Stack

| Layer            | Technology                     | Purpose                            |
|------------------|--------------------------------|------------------------------------|
| **Language**     | Java 17                        | Backend logic                      |
| **Framework**    | Spring Boot                    | API service and worker logic       |
| **Messaging**    | Redis, Kafka/RabbitMQ          | Task queueing, DLQ, pub-sub        |
| **Database**     | PostgreSQL                     | Job persistence                    |
| **Notifications**| Spring Mail / HTTP Webhooks    | Notify clients on job completion  |
| **Auth**         | JWT + RBAC                     | API-level security                 |
| **Monitoring**   | Prometheus, Grafana            | Metrics and dashboards             |
| **CI/CD**        | GitHub Actions                 | Build, test, deploy automation     |
| **Containers**   | Docker, Docker Compose         | Containerization for local use     |
| **Orchestration**| Kubernetes (Minikube/K3s)      | Scalable deployment                |
| **Config Mgmt**  | YAML / AWS SSM                 | Load environment-specific configs  |

---


## 🔁 **High-Level Project Flow (Overview)**

```
Client → API Service → Redis Queue → Worker Pool → PostgreSQL / Kafka (DLQ) → Notifier → Monitoring
```

---

## 🚦 **Step-by-Step Execution Flow**

---

### **1. Task Submission (Client → API Service)**

- A client sends an HTTP `POST /tasks` request to the API with job details like:
  ```json
  {
    "type": "email",
    "payload": { "to": "user@example.com", "subject": "Welcome" },
    "delaySeconds": 60,
    "retries": 3,
    "dependencies": []
  }
  ```
- The API performs:
  - Validation of input payload
  - Generates a unique `taskId`
  - Stores metadata in **PostgreSQL** (status = `PENDING`)
  - Pushes the task into **Redis** with:
    - Delay timer
    - Priority score
    - Job UUID for idempotency

---

### **2. Task Queueing (Redis Sorted Set or List)**

- Redis acts as a delay/priority queue:
  - Jobs are stored in sorted sets based on `executeAt` timestamp
  - Priority determines ordering among same-time tasks
- If DAG is enabled, task won’t be released until its dependencies are completed

---

### **3. Task Fetching (Worker → Redis)**

- The **Worker Pool** polls Redis regularly:
  - Picks up eligible tasks (whose delay has expired)
  - Acquires a distributed lock (SETNX) to prevent double-processing
- Checks if task is:
  - Already running or completed (to ensure idempotency)
  - In a DAG → checks dependencies before execution

---

### **4. Task Execution (Worker → Task Logic)**

- Executes the job (e.g., sending an email, HTTP call, etc.)
- Based on outcome:
  - ✅ On success: updates **PostgreSQL** as `SUCCESS`, stores logs
  - ❌ On failure:
    - Reduces retry count
    - Reschedules in Redis (if retries > 0)
    - Sends to **Kafka DLQ** if all retries exhausted

---

### **5. Job Notifications (Notifier Service)**

- Once a job completes (success/failure), the **Notifier Service**:
  - Sends an email or webhook callback to the client (based on config)
  - Includes job ID, status, logs, and timestamps

---

### **6. DAG Dependency Handling (Topological Sort)**

- If a task has dependencies (DAG):
  - A directed acyclic graph is maintained in metadata
  - After a task finishes, the DAG executor:
    - Updates status of downstream tasks
    - Unlocks the next task if all its dependencies are complete

---

### **7. Monitoring & Observability**

- Every component emits metrics:
  - API: request count, response time, failures
  - Worker: job count, retry attempts, success/failure rate
  - Redis Queue: queue size, wait time
- **Prometheus** scrapes these metrics
- **Grafana** displays dashboards for:
  - Task status trends
  - Retry spikes
  - Throughput per minute
  - DLQ rate

---

### **8. Configuration Management**

- Config values like `MAX_RETRIES`, `NOTIFICATION_TYPE`, `WORKER_CONCURRENCY` are loaded from:
  - YAML (for local/dev)
  - AWS SSM (for cloud/production)
- Config Server broadcasts updates dynamically if changed

---

### **9. Deployment Flow (CI/CD)**

- **GitHub Actions**:
  - On push to `main`, it:
    - Runs tests
    - Builds Docker image
    - Pushes to DockerHub
- **Kubernetes**:
  - Pulls images
  - Deploys services (API, worker, notifier, monitoring) as pods

---

## 🧠 Example: What Happens When a Task Fails?

1. Task fails due to a network error
2. Retry count is decremented (e.g., 3 → 2)
3. Task is rescheduled in Redis with backoff (e.g., wait 60s)
4. On final failure (retry count 0), task is pushed to Kafka DLQ
5. DLQ worker logs it, marks task as `FAILED` in DB
6. Notifier informs the client via webhook: `"status": "FAILED"`

---

## 🧠 Example: DAG Execution Scenario

1. Task A has two child tasks: B and C
2. Client submits A → system stores DAG: A → [B, C]
3. A is executed first
4. Once A succeeds, DAG resolver releases B and C into queue
5. B and C execute in parallel

---

## ✅ Summary

This project implements:
- Asynchronous, reliable, delayed job execution
- Retry/backoff + fault-tolerant workers
- DAG-based dependency resolution
- Full observability with logs and metrics
- Real-world deployment setup (Docker + Kubernetes)

---

### ✨ Highlights
- Scalable and modular design
- Production-style CI/CD and deployment
- Real-world use of Redis, Kafka, and monitoring
- Feature-rich scheduler with retries, delays, and DAGs
- Academic focus on distributed architecture with practical implementation
- The architecture is designed to scale horizontally and handle real-world backend job scheduling scenarios, making it suitable for enterprise workloads and cloud-native applications.
