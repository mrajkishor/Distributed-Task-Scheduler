> Kanban : https://github.com/users/mrajkishor/projects/7 


## 🗓️ **Extended 6-Week Schedule: April 7 – May 15, 2025**

### 🔹 Week 1 (Apr 7–13): Setup & Core Scheduling
| Day | Task |
|-----|------|
| Apr 7 (Mon) | Define architecture, API design, job model, Redis schema |
| Apr 8 (Tue) | Setup Spring Boot modules & basic GitHub repo |
| Apr 9 (Wed) | Docker Compose setup with Redis, Kafka, PostgreSQL |
| Apr 10 (Thu) | Implement job submission API (POST `/tasks`) |
| Apr 11 (Fri) | Store tasks in Redis with delay/priority handling |
| Apr 12 (Sat) | Build basic Worker Service to process jobs |
| Apr 13 (Sun) | Track job status in Redis and expose GET `/tasks/{id}` |

---

### 🔹 Week 2 (Apr 14–20): Retry, DLQ, Idempotency
| Day | Task |
|-----|------|
| Apr 14 (Mon) | Implement retry mechanism with exponential backoff |
| Apr 15 (Tue) | Setup Dead Letter Queue using Redis/Kafka |
| Apr 16 (Wed) | Add deduplication & idempotency using job UUID |
| Apr 17 (Thu) | API validations and error handling improvements |
| Apr 18 (Fri) | Unit + integration testing of API and Worker |
| Apr 19 (Sat) | Code cleanup and modularization |
| Apr 20 (Sun) | API contract finalization (Swagger/OpenAPI)

---

### 🔹 Week 3 (Apr 21–27): Observability, Auth, Docker
| Day | Task |
|-----|------|
| Apr 21 (Mon) | Integrate Prometheus metrics in API + Worker |
| Apr 22 (Tue) | Setup Grafana dashboards for job execution metrics |
| Apr 23 (Wed) | Add structured logging using SLF4J (JSON format) |
| Apr 24 (Thu) | Secure APIs with JWT authentication |
| Apr 25 (Fri) | Dockerize all services |
| Apr 26 (Sat) | Run full workflow using `docker-compose` |
| Apr 27 (Sun) | Basic deployment test on local/EC2

---

### 🔹 Week 4 (Apr 28 – May 4): Enhancements 🚀
| Date | Task |
|------|------|
| Apr 28 (Mon) | Add job notification via email/webhook (Spring Mail / HTTP call) |
| Apr 29 (Tue) | Implement Job Dependencies / Chaining (simple DAG logic) |
| Apr 30 (Wed) | Build webhook receiver mock server to test notifications |
| May 1 (Thu)  | Write scheduler config: delay strategies, intervals, etc. |
| May 2 (Fri)  | Enable horizontal worker scaling test (multi-instance simulation) |
| May 3 (Sat)  | Add feature to pause/resume workers dynamically |
| May 4 (Sun)  | Add config file loading via SSM / YAML profiles

---

### 🔹 Week 5 (May 5–11): K8s, CI/CD, Final Polish
| Date | Task |
|------|------|
| May 5 (Mon)  | Create basic Kubernetes YAMLs for all services |
| May 6 (Tue)  | Deploy locally using Minikube or K3s |
| May 7 (Wed)  | Setup GitHub Actions for CI/CD (build, test, Docker push) |
| May 8 (Thu)  | Finalize README with architecture diagram + usage |
| May 9 (Fri)  | Add Postman collection + API documentation |
| May 10 (Sat) | Add sample payloads, logs, test files, CLI script |
| May 11 (Sun) | Create GitHub release `v1.0` and backup repo

---

### 🔹 Week 6 (May 12–15): Backup + Begin MCA Documentation
| Date | Task |
|------|------|
| May 12 (Mon) | Take screenshots of APIs, logs, Grafana, job status |
| May 13 (Tue) | Start documentation (cover page, summary, process design) |
| May 14 (Wed) | Continue report + insert diagrams from codebase |
| May 15 (Thu) | Final check of code + begin full report writing smoothly ✅

