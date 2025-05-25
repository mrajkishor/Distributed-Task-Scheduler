## 🚦 Graceful Shutdown & Health Monitoring

This system supports graceful shutdown and robust health monitoring, ensuring reliability and observability in production environments.

### 🔄 Graceful Shutdown

- Listens to OS signals (`SIGINT`, `SIGTERM`) via Spring Boot’s `ContextClosedEvent`
- Shuts down active `ThreadPoolTaskExecutor` without interrupting running jobs
- Releases Redis locks for in-progress tasks to avoid deadlocks
- Logs structured shutdown events (start, cleanup, complete) visible in ELK stack
- Designed to safely requeue or move incomplete jobs to a DLQ (extendable)

### 📡 Health Monitoring

- Exposes actuator endpoints:
  - `/actuator/health` — Overall health
  - `/actuator/health/readiness` — Readiness probe
  - `/actuator/health/liveness` — Liveness probe
  - `/actuator/prometheus` — Prometheus scrape target
- Includes built-in and custom health indicators:
  - ✅ Redis connectivity
  - ✅ Disk space check
  - ✅ Queue size and pressure (`QueueHealthIndicator`)
- Secured via HTTP Basic Auth (`admin / secret123`) for Prometheus & monitoring tools

> ℹ️ Spring Boot Actuator + Prometheus + Grafana integration is production-ready.


Featured developed with [#93](https://github.com/mrajkishor/Distributed-Task-Scheduler/issues/93)