# ⚙️ Distributed Task Scheduler

A **high-performance, distributed task scheduling system** designed to handle delayed jobs, background processing, retries, and real-time job orchestration at scale. Inspired by systems like Celery, Sidekiq, and Quartz, this project demonstrates key concepts in distributed systems, backend engineering, and system design—aligned with industry expectations.

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
## 👨‍🏫 Project Guide

**BALAKRUSHNA BEHERA** - Java Backend Developer | MCA | 3+ years of industry experience

[LinkedIn](https://www.linkedin.com/in/balakrushna-behera-5687001a2/)


**Abhishek Prasad** - Software Engineer | NIT Durgapur

[LinkedIn](https://www.linkedin.com/in/compro-prasad/)

---

## 📜 License

MIT License
