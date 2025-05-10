## ✅ Step-by-Step Project Checklist (MCA Final Year Project)

---

### 🔹 Step 1: Core Setup & Architecture
- [x] [1.1 Define architecture, detailed system design diagram, tech stack](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/29)  
- [x] [1.2 Design APIs (OpenAPI), job model (with DAG support), Redis schema](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/31)  
- [x] [1.3 Setup Spring Boot modules, project structure, base entities](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/56)  
- [x] [1.4 Configure Docker Compose: Redis, Kafka, PostgreSQL](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/58)  
- [x] [1.5 Implement job submission API (POST `/tasks`)](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/60)  
- [x] [1.6 Store tasks in Redis with priority/delay/tenant support](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/62)  
- [x] [1.7 Track job status in Redis and expose GET `/tasks/{id}` endpoint](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/64)
- [x] [1.8 Initialize GitHub repository with README and project setup](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/66)  

---

### 🔹 Step 2: Retry Logic, Idempotency, and DAG Execution
- [ ] [2.1 Implement retry mechanism (exponential backoff + max attempts)](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/68)  
- [ ] 2.2 Setup Dead Letter Queue (DLQ) using Redis/Kafka  
- [ ] 2.3 Add idempotency and deduplication using UUID/hash keys  
- [ ] 2.4 Implement DAG traversal, topological sort, and cycle detection  
- [ ] 2.5 Build DAG executor to manage dependent task execution  
- [ ] 2.6 Write unit + integration tests (API + DAG processor)  
- [ ] 2.7 Finalize API contracts and handle edge cases (Swagger/OpenAPI)  
- [ ] 2.8 Refactor codebase for modularity and cleanup  

---

### 🔹 Step 3: Observability, Fault Tolerance, Security
- [ ] 3.1 Integrate Prometheus metrics (queue size, execution time, failures)  
- [ ] 3.2 Set up Grafana dashboards  
- [ ] 3.3 Add structured logging using SLF4J (JSON, correlation IDs)  
- [ ] 3.4 Implement JWT-based auth with role-based access control  
- [ ] 3.5 Add distributed locking for task leasing (Redis SETNX or Redisson)  
- [ ] 3.6 Implement graceful shutdown, signal handling, and health checks  
- [ ] 3.7 Run and validate full task lifecycle: submit ➝ lease ➝ execute ➝ status  

---

### 🔹 Step 4: Scaling, Notification, Throttling
- [ ] 4.1 Add job notification via email/webhook  
- [ ] 4.2 Build a webhook receiver mock server  
- [ ] 4.3 Implement horizontal scaling of workers (multi-instance test)  
- [ ] 4.4 Add rate limiting/throttling via Redis token bucket  
- [ ] 4.5 Add support to pause/resume workers dynamically  
- [ ] 4.6 Implement config loading via SSM / YAML profiles  
- [ ] 4.7 Add job versioning, tag-based filtering, and label support  

---

### 🔹 Step 5: Deployment, DevOps, CI/CD
- [ ] 5.1 Write Kubernetes YAMLs for all services  
- [ ] 5.2 Deploy services locally via Minikube/K3s  
- [ ] 5.3 Set up GitHub Actions for CI (test/build/lint) and CD (Docker push)  
- [ ] 5.4 Secure services using RBAC and secrets management  
- [ ] 5.5 Conduct load testing with k6 or Locust and analyze logs/metrics  
- [ ] 5.6 Prepare Postman collection, logs, and sample test payloads  
- [ ] 5.7 Finalize README with architecture diagrams, metrics, usage guide  
- [ ] 5.8 Perform deployment test on EC2 (optional for real-world testing)  

---

### 🔹 Step 6: Documentation & Final Delivery
- [ ] 6.1 Take screenshots of dashboards, APIs, logs, Grafana metrics  
- [ ] 6.2 Begin writing final MCA report: cover page, intro, methodology  
- [ ] 6.3 Add system design diagrams: architecture, DB schema, flowcharts  
- [ ] 6.4 Final code review, polish codebase, and create GitHub release `v1.0`  

---

### 🔹 Step 7: MCA Report Formatting & Submission
- [ ] 7.1 Prepare Abstract/Summary (3–4 pages)  
- [ ] 7.2 Write Acknowledgement section  
- [ ] 7.3 Fill in Guide Certificate (Annexure A format)  
- [ ] 7.4 Clearly define Objective & Scope of the project  
- [ ] 7.5 Add Process Description with Flowchart/DFD/UML  
- [ ] 7.6 Describe Methodology adopted, implementation steps  
- [ ] 7.7 Include Testing methodology and attach test reports  
- [ ] 7.8 Create User Manual (access, backup, security, roles)  
- [ ] 7.9 Build Data Dictionary (data fields, types, format)  
- [ ] 7.10 Compile screenshots of input/output UIs and system logs  
- [ ] 7.11 Prepare Bibliography/References section  
- [ ] 7.12 Submit soft copy to LMS and bind hard copy as per format  

