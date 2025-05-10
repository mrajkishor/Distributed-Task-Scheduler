
# 🧠 Distributed Task Scheduler (DAG-Based Execution Engine)

> MCA Final Year Project – Developed with Java, Spring Boot, Redis, Kafka, and Docker

---

## 📌 Project Overview

**Distributed Task Scheduler** is a scalable, fault-tolerant job execution engine that orchestrates task execution with support for **Directed Acyclic Graphs (DAGs)**. It ensures reliable scheduling, retry logic, and tenant-based task isolation using Redis and Kafka.

This project was built as part of the MCA final semester and reflects practical application of backend architecture, distributed queues, and microservice principles.

---

## 🚀 Features

- ✅ Submit tasks with dependencies using REST APIs
- ✅ Redis ZSET-based delay queue for scheduled execution
- ✅ Tenant-aware Redis keys (`task:{tenantId}:{taskId}`)
- ✅ Retry support with backoff logic (planned)
- ✅ DAG execution with topological sort (in progress)
- ✅ REST APIs to track task status
- ✅ Pluggable for email/webhook notifications
- ✅ Docker + Spring Boot microservice architecture

---

## 🛠 Tech Stack

| Layer         | Technologies                     |
|---------------|----------------------------------|
| Backend       | Java 17, Spring Boot             |
| Messaging     | Redis (ZSET + Hash), Kafka       |
| Persistence   | Redis (primary), PostgreSQL (optional) |
| DevOps        | Docker, Docker Compose           |
| Monitoring    | Prometheus, Grafana (planned)    |
| Testing       | JUnit, MockMvc (planned)         |

---

## 📦 API Endpoints

| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| POST   | `/tasks`           | Submit a task with DAG info |
| GET    | `/tasks/{id}`      | Fetch task status by ID  |

> ✅ Multi-tenant headers supported: `X-Tenant-ID: clientA`

---

## ⚙️ Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/mrajkishor/Distributed-Task-Scheduler.git
cd Distributed-Task-Scheduler
````

### 2. Run Redis and Kafka

```bash
docker-compose up -d
```

### 3. Start Spring Boot App

```bash
cd core
./mvnw spring-boot:run
```

> App will be running at `http://localhost:8081`

---

## 📁 Project Structure

```
Distributed-Task-Scheduler/
├── core/           # Spring Boot service
├── runner/         # Executable entry point
├── docs/           # Diagrams, screenshots
├── scripts/        # Dev/utility scripts (optional)
├── README.md       # You're reading it
├── .gitignore      # Git ignore config
└── LICENSE         # MIT/Apache license
```

---

## ✍️ Author

**Rajkishor Maharana**
MCA Final Year – KIIT University (Online)
📧 [mrajkishor331@gmail.com](mailto:mrajkishor331@gmail.com)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

