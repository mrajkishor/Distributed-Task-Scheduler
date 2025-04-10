# 🔧 Scalability Considerations – Distributed Task Scheduler

This document outlines future-proof features for enhancing the scalability and flexibility of the task scheduling system.

---

## ⏸️ 1. Pause / Resume Jobs

### ✨ Purpose
Allow temporarily pausing a job (stops future executions) and resuming it when needed.

### 📌 Proposed Endpoints
- `POST /jobs/{id}/pause`
- `POST /jobs/{id}/resume`

### 🧠 Design Notes
- Add `status` field in Job model (`ACTIVE`, `PAUSED`, etc.)
- Skip triggering paused jobs
- Resume should restore previous scheduling behavior

---

## 🔁 2. Retry Policies

### ✨ Purpose
Handle task failures gracefully by retrying them with configurable strategies.

### 📌 Job Schema Field
```json
"retryPolicy": {
  "type": "object",
  "properties": {
    "strategy": { "enum": ["NONE", "ON_FAILURE", "ALWAYS"] },
    "maxRetries": { "type": "integer", "default": 3 },
    "backoff": { "enum": ["FIXED", "EXPONENTIAL"], "default": "FIXED" },
    "retryDelayMs": { "type": "integer", "default": 1000 }
  }
}
```

### 🧠 Design Notes
- Retry only failed tasks
- Maintain retry counters in Redis
- Allow pluggable retry handlers in future

---

## ⏰ 3. Cron Scheduling

### ✨ Purpose
Enable recurring job executions using cron-like expressions.

### 📌 Job Schema Field
```json
"schedule": "cron(0 10 * * ? *)"
```

### 🧠 Design Notes
- Support standard cron syntax or AWS-style `cron(...)`
- Use a cron parser to calculate `nextExecutionTime`
- Store schedule metadata in Redis (optional)

---

## 📈 Future Enhancements
- Rate limiting / concurrency limits per job
- Job priority levels (`HIGH`, `MEDIUM`, `LOW`)
- Scheduled job groups or namespaces

---

## ✅ Summary

| Feature       | Supported Now | Planned In Design |
|---------------|----------------|------------------|
| Pause/Resume  | ❌              | ✅                |
| Retry Policy  | ✅ (schema)     | ✅                |
| Cron Schedule | ✅ (schema)     | ✅                |
