
## ✅ Source Structure (Java + Resources)

```
Distributed-Task-Scheduler/
└── runner/
    ├── src/main/java/com/distributedscheduler/
    │   ├── config/           → ✅ Configuration classes
    │   ├── consumer/         → ✅ Kafka worker or executor
    │   ├── controller/       → ✅ REST APIs (e.g. JobController)
    │   ├── core/             → ✅ Entry point (CoreApplication.java)
    │   ├── dto/              → ✅ Optional for request/response models
    │   ├── helper/           → ✅ Helpers (util, common logic)
    │   ├── model/            → ✅ Your full domain model (Job, Task, etc.)
    │   ├── repository/       → ✅ Redis/PostgreSQL repo if needed
    │   ├── service/          → ✅ Business logic layer
    │   └── util/             → ✅ Utility like DAG logic (DagUtils.java)
    └── src/main/resources/
        ├── application.yaml  → ✅ Main config file
```

