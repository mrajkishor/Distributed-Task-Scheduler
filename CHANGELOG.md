# 📦 Changelog

## [v1.0-core] – Core Features Release (till Step 4.1)
> 🚀 Released: 2025-05-27

This release includes all completed features till **Step 4.1**, excluding PostgreSQL and Kafka integration. It marks the completion of the core system design, task scheduling, observability, authentication, and notification system via webhooks.

---

### ✅ Added

#### 🔹 Core Features
- Task submission via `POST /tasks` with DAG dependencies
- Redis-based task queue with delay, priority, and tenant isolation
- Task lifecycle execution: lease ➝ run ➝ complete ➝ status update
- Status query via `GET /tasks/{id}`

#### 🔹 DAG Orchestration
- Dependency modeling with `dependencies` array
- DAG cycle detection
- Topological sort execution engine

#### 🔹 Retry & Fault Tolerance
- Retry with exponential backoff and max retry limit
- Dead Letter Queue (Redis fallback)
- Distributed locking with Redis SETNX

#### 🔹 Observability
- Prometheus metrics for queue size, success/failures, processing time
- Grafana dashboards
- JSON structured logs with traceId + correlationId
- ELK Stack (Logstash + Kibana)

#### 🔹 Security
- JWT authentication
- Role-based access control (`admin`, `user` roles)

#### 🔹 Notifications
- Webhook support for task completion/failure

#### 🔹 DevOps
- Docker Compose setup for Redis, PostgreSQL, Kafka, Prometheus, Grafana, Logstash, Kibana

---

### 🚫 Excluded (Next Milestone)

- PostgreSQL persistence for task metadata
- Kafka event publishing for analytics + DLQ
