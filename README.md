# ⚙️ Distributed Task Scheduler

A **high-performance, distributed task scheduling system** designed to handle delayed jobs, background processing, retries, and real-time job orchestration at scale. Inspired by systems like Celery, Sidekiq, and Quartz, this project demonstrates key concepts in distributed systems, backend engineering, and system design—aligned with industry expectations.

---

### 🧾 **Abstract**

This project presents the design and development of a **Distributed Task Scheduler** that orchestrates task execution based on **Directed Acyclic Graphs (DAGs)**. Built using **Spring Boot, Kafka, Redis, and Kubernetes**, the system ensures reliable, scalable, and fault-tolerant execution of interdependent tasks across distributed nodes. The scheduler supports real-time execution, failure recovery, retry mechanisms, and dependency management. It is suitable for cloud-native workloads such as data pipelines, CI/CD processes, and backend automation workflows.

---

### ❗ **Problem Statement**

Modern enterprise systems require execution of complex workflows that involve multiple interdependent tasks across distributed environments. Traditional schedulers lack scalability, dynamic task control, or failover handling. This project addresses the problem by developing a **DAG-based distributed task scheduler** that efficiently manages task dependencies, execution order, and fault tolerance, providing a modular, scalable, and reusable solution for orchestrating cloud-native operations.

### 🔧 **Examples of Traditional Schedulers & Their Limitations**

| **Traditional Scheduler** | **Limitations Compared to Your Project** |
|---------------------------|------------------------------------------|
| **Cron / crontab (Linux)** | - No support for task dependencies<br>- No retry/failure handling<br>- No distribution across nodes<br>- Hard-coded/static schedule |
| **Quartz Scheduler** | - Mostly designed for monolithic or single-node systems<br>- Limited support for distributed fault-tolerant execution<br>- Lacks native DAG task orchestration |
| **Spring @Scheduled** | - Embedded in single-instance Spring apps<br>- No parallel or distributed task execution<br>- No dependency resolution |
| **Windows Task Scheduler** | - Single-machine only<br>- Not cloud-native<br>- No concept of task graphs or fault recovery |
| **Airflow (Pre-optimized)** | - Airflow itself is DAG-based and powerful, but not lightweight for embedded use<br>- Setup overhead for small or custom environments |


### ✅ **What this Project Improves**

- **DAG awareness** → Executes tasks **only after dependencies complete**
- **Distributed execution** → Tasks can run across nodes (via Kafka or REST)
- **Resilience** → Uses **Redis** to track states; retries failed tasks
- **Scalability** → Can handle many parallel workflows dynamically
- **Modular** → Pluggable and lightweight compared to heavy-duty schedulers like Airflow

In essence, this project fills the gap where traditional tools are either **too simple (like cron)** or **too heavy (like Airflow)** by offering a middle-ground: **lightweight, DAG-aware, distributed, and scalable**.


> Traditional schedulers such as `cron`, `Quartz`, and `Spring's @Scheduled` are widely used for basic task automation but fall short when it comes to handling complex, interdependent workflows in a distributed environment. These tools typically lack support for dynamic task control, dependency resolution, failover handling, and scalable execution across multiple nodes. For example, `cron` and `Quartz` operate on static time-based triggers without awareness of task dependencies, while `Spring @Scheduled` is limited to single-instance execution within a monolithic application. Even more advanced solutions like Apache Airflow offer DAG-based scheduling but are often heavyweight and require significant setup, making them less suitable for lightweight or embedded use cases. This project addresses these limitations by developing a DAG-based Distributed Task Scheduler that offers a modular, scalable, and fault-tolerant approach to orchestrating cloud-native operations. By leveraging technologies like Spring Boot, Redis, Kafka, and Kubernetes, it enables efficient task orchestration with built-in support for retries, execution tracking, and dynamic dependency resolution.


---

### 🔬 **Research Scope (for Distributed Task Scheduler)**

The project covers an **exploratory and empirical study** in the field of **distributed systems and cloud-native task orchestration**. It investigates the design and implementation of a **fault-tolerant, DAG-based task scheduling system** using modern backend technologies like Spring Boot, Kafka, Redis, and Kubernetes. The system is applicable to real-world scenarios such as workflow automation, CI/CD pipelines, and data engineering job execution. The research may target a **general industry area**—specifically **cloud infrastructure and DevOps automation**—and can also be adapted for a **specific enterprise use case** like internal microservices coordination or multi-tenant job execution systems.


---

### 📊 **Data Requirements**

This project primarily utilizes **secondary data** in the form of system-generated task metadata, execution logs, and DAG structures. The scheduler does not rely on external real-world datasets but instead works with internally defined task definitions, job dependencies, and runtime status data. The data includes:

- **Task metadata**: Task ID, task name, type, command to execute, retry count, timeout, etc.  
- **DAG structure**: Representation of task dependencies (edges and nodes)  
- **Job metadata**: Job ID, user ID (if multi-tenant), job status (pending, running, failed, complete)  
- **Execution logs**: Timestamps, outcomes, retries, error messages  
- **Redis Data**: Stores task state, queues, and interim progress  
- **Kafka Events**: Used for task communication and status propagation across services

Thus, the project uses a **combination of simulated primary data** (created by the scheduler itself during runtime) and **secondary system-level data**, which are both crucial for evaluating task execution, fault tolerance, and performance analysis.


### 📊 **Table: Data Requirements for Distributed Task Scheduler**

| **Data Type**         | **Description**                                                                 | **Source**                        | **Usage**                                        |
|----------------------|----------------------------------------------------------------------------------|-----------------------------------|--------------------------------------------------|
| **Task Metadata**     | Task ID, name, type, command, retries, timeout                                  | Defined by user or system config | Used to schedule and execute individual tasks    |
| **DAG Structure**     | Nodes (tasks) and Edges (dependencies between tasks)                            | User-defined / System-generated  | Determines execution order based on dependencies |
| **Job Metadata**      | Job ID, job name, user ID, status (pending/running/failed/completed)            | Internally generated              | Tracks overall workflow execution                |
| **Execution Logs**    | Start time, end time, success/failure status, error messages                    | Captured during runtime           | Used for debugging and audit                     |
| **Redis Data Store**  | Task states, queues, retry counters, heartbeat/status values                    | System-level                      | Enables distributed state management             |
| **Kafka Events**      | Task execution commands, status updates, retry triggers                         | System-generated                  | Enables asynchronous and decoupled communication |


### 📁 **Sample Mock Data for Distributed Task Scheduler**

#### 🧩 1. **Task Metadata**
```json
{
  "taskId": "task_101",
  "taskName": "SendEmail",
  "type": "email",
  "command": "python send_email.py",
  "retries": 3,
  "timeout": 60
}
```

#### 🔗 2. **DAG Structure**
```json
{
  "jobId": "job_202",
  "tasks": ["task_101", "task_102", "task_103"],
  "dependencies": {
    "task_102": ["task_101"],
    "task_103": ["task_102"]
  }
}
```

#### 📋 3. **Job Metadata**
```json
{
  "jobId": "job_202",
  "jobName": "DailyReportPipeline",
  "userId": "user_1",
  "status": "running"
}
```

#### 📝 4. **Execution Logs**
```json
{
  "taskId": "task_101",
  "startTime": "2025-04-20T10:00:00Z",
  "endTime": "2025-04-20T10:00:05Z",
  "status": "success",
  "logs": "Email sent successfully to 300 users."
}
```

#### 🧠 5. **Redis Entry (Sample)**
```json
{
  "key": "job_202:task_101:status",
  "value": "completed"
}
```

#### 🔄 6. **Kafka Event**
```json
{
  "eventType": "TASK_COMPLETE",
  "jobId": "job_202",
  "taskId": "task_101",
  "status": "success",
  "timestamp": "2025-04-20T10:00:05Z"
}
```


---

## 🧾 Project Overview

**Title:** Design and Implementation of a Distributed Task Scheduler for Scalable Background Job Processing  
**Objective:** To build a scalable, fault-tolerant system for managing time-based and background jobs in a distributed environment.

>This project was developed as part of my academic coursework in the MCA program, with a focus on distributed systems and backend engineering.

---

## 🔥 Core Features

- 🧠 Job queuing and priority support
- ⏳ Delayed and recurring job scheduling
- 🔁 Retry with exponential backoff
- 💥 Dead-letter queue for failed jobs
- 🧵 Multi-threaded worker execution
- 🚨 Real-time monitoring and job status tracking
- ✅ Idempotency and fault tolerance
- 🐳 Dockerized and ready for deployment

---

## 🚀 Advanced Enhancements

- 🔗 Job dependencies and chaining
- 📩 Webhook & email notifications on job completion/failure
- 🔐 JWT-based API authentication
- 📈 Prometheus + Grafana dashboards for metrics
- 📦 CI/CD integration with GitHub Actions
- ⚙️ Configurable job scheduler profiles
- ☸️ Kubernetes deployment support
- ⚠️ Dynamic pause/resume of workers
- 🧪 Load testing scripts for benchmarking

---

## 💻 Tech Stack

| Layer         | Technology                     |
|---------------|--------------------------------|
| Language      | Java 17                        |
| Framework     | Spring Boot                    |
| Messaging     | Kafka / RabbitMQ               |
| Storage       | Redis / PostgreSQL             |
| Orchestration | Docker / Docker Compose        |
| Monitoring    | Prometheus + Grafana           |
| CI/CD         | GitHub Actions                 |
| Deployment    | Kubernetes (Minikube)          |

---

## 🏗️ Architecture

```
Client → REST API (Spring Boot)
             ↓
        Kafka Queue
             ↓
        Task Processor (Worker Pool)
             ↓
   - Execute Task Logic
   - Retry or DLQ on Failure
             ↓
     Update Task Status in Redis
```

![flow_diagra_dag](https://github.com/user-attachments/assets/b9cd0d38-1927-4bf9-8901-e3e29e485a6f)



---

### 🎨 UI

Dashboard
> <img width="1128" height="850" alt="image" src="https://github.com/user-attachments/assets/ec8aa242-1c24-4e17-9c74-eaa38dbb59f8" />

Tasks 
> <img width="1137" height="849" alt="image" src="https://github.com/user-attachments/assets/65c2c368-9692-4aaa-8274-2dd431e05f38" />

DAG Visualizer 
> <img width="1132" height="851" alt="image" src="https://github.com/user-attachments/assets/be04f93e-000d-405f-8aa9-723e38cb46cb" />

DLQ
> <img width="1139" height="848" alt="image" src="https://github.com/user-attachments/assets/2a3ee680-e4b8-456f-981d-7951ef0c1018" />

Other screens | Work in progress

---

## 📦 Project Modules

- `api-service`: REST endpoints to submit tasks
- `scheduler-core`: Core scheduling logic and cron handling
- `task-consumer`: Kafka/RabbitMQ consumer and executor
- `monitoring`: Prometheus metrics and health checks
- `notifier-service`: Sends webhook/email notifications
- `config-server`: Loads job configs dynamically
- `k8s-deployment`: YAMLs for container orchestration

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Apache Kafka or RabbitMQ

### Setup

```bash
# Clone the repo
git clone https://github.com/your-username/distributed-task-scheduler.git
cd distributed-task-scheduler

# Start required services
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

---

## 🧪 Example API Usage

```http
POST /tasks/schedule

{
  "type": "email",
  "payload": { "to": "user@example.com", "subject": "Hello" },
  "delaySeconds": 30,
  "retries": 3
}
```

---

## 📊 Monitoring

- Prometheus metrics at `/actuator/prometheus`
- Grafana dashboard visualizing:
  - Job status count
  - Execution time
  - Retry and failure rate

---

## 📚 Relevance

This project showcases expertise in:
- Distributed Systems & Message Queues
- Cloud-Native Backend Architecture
- Fault-Tolerant, Scalable Services
- Observability and DevOps Practices
- Real-World Microservices Development

---

## 🙋‍♂️ Author

**Raj Kishor Maharana** – Full stack developer | Exploring Distributed Systems  

[LinkedIn](https://www.linkedin.com/in/mrajkishor331/) • [Email](mailto:mrajkishor331@gmail.com)

---

## 📜 License

MIT License
