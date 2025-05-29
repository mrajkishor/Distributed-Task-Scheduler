## Use of Domain-Driven Design (DDD)

Although the project is implemented as a modular monolith, it adopts key principles of Domain-Driven Design (DDD) to maintain clean separation of concerns and domain-centric logic. The following DDD elements are applied:

### ✅ Core Domain Isolation
The central domain of the system is task scheduling and orchestration. Business logic related to task states, execution flow, DAG traversal, retries, and idempotency is encapsulated within dedicated classes like `Task`, `DAGExecutor`, and `RetryService`.

### ✅ Entities and Value Objects
- `Task` is treated as a domain entity, maintaining its identity and lifecycle across states such as `PENDING`, `RUNNING`, `FAILED`, and `COMPLETED`.
- Supporting attributes like `RetryCount`, `Status`, and `TaskId` could be considered as value objects encapsulating specific rules.

### ✅ Application Services
All core business logic is handled within service classes rather than being mixed with controller or persistence code. For example:
- `TaskSchedulerService` manages DAG evaluation and task readiness.
- `RetryHandlerService` encapsulates retry backoff logic.

### ✅ Repository Abstraction
The application uses repository interfaces such as `TaskRedisRepository` to abstract Redis operations. This aligns with the DDD repository pattern by separating domain logic from infrastructure concerns.

### ✅ Modular Bounded Contexts (Implicit)
The system separates concerns by design:
- Task Submission
- DAG Evaluation
- Task Execution
- Monitoring & Notifications

Each functional area is loosely coupled, preparing the system for future microservice extraction if needed.

---

By adhering to DDD principles within a modular monolith, the system remains maintainable, scalable, and well-aligned with real-world business workflows.
