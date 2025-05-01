
# 🔄 Project Execution Flow – Distributed Task Scheduler

---

## 🚦 Step-by-Step Execution Flow

### **1. Task Submission (Client → API Service)**
- Client sends POST `/tasks` with payload (type, delay, retries, etc.)
- API:
  - Validates request
  - Generates unique `taskId`
  - Stores metadata in PostgreSQL (`PENDING`)
  - Queues job in Redis with delay/priority/idempotency key

---

### **2. Task Queueing (Redis)**
- Redis holds jobs using sorted sets (by `executeAt` timestamp)
- Priority jobs sorted higher
- DAG jobs wait until dependencies finish

---

### **3. Task Fetching (Worker → Redis)**
- Worker polls Redis for due jobs
- Acquires distributed lock (SETNX)
- Ensures task isn’t already running/completed
- Checks DAG dependency before execution

---

### **4. Task Execution (Worker)**
- Executes job logic (e.g., send email)
- Updates PostgreSQL:
  - ✅ On success: `SUCCESS`
  - ❌ On failure:
    - Retry if retries left (exponential backoff)
    - Else push to Kafka DLQ

---

### **5. Notifications (Notifier Service)**
- Sends email/webhook on job `SUCCESS` or `FAILED`
- Includes job ID, timestamps, and log info

---

### **6. DAG Dependency Handling**
- Maintains task DAG in DB
- When parent task completes:
  - Checks child dependencies
  - Releases ready child jobs to Redis

---

### **7. Monitoring (Prometheus + Grafana)**
- Metrics exported from API and workers:
  - Job counts, failures, retries
  - Redis queue size, task wait time
- Grafana shows visual dashboards

---

### **8. Configuration Management**
- Configs loaded from YAML or AWS SSM
- Worker behavior controlled without redeploying

---

### **9. CI/CD + Deployment**
- GitHub Actions pipeline:
  - Test → Build → Dockerize → Push
- Kubernetes (Minikube/K3s) deployment:
  - All services deployed as pods

---

## 🧠 Real-World Scenarios

### 🔁 Task Retry Flow:
- On failure:
  - Decrease retry count
  - Re-queue with delay
  - On final failure → DLQ + notify user

### 🌐 DAG Flow:
- Task A → [Task B, Task C]
- Execute A → release B & C
- Execute B, C in parallel after A’s success

---

## ✅ Summary
- Fully async, reliable, observable job scheduling
- Retry, DAG, and DLQ supported
- CI/CD + Docker + Kubernetes ready

