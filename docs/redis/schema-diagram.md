

# 🗂️ Redis Schema Diagram for Distributed Task Scheduler

```
┌─────────────────────────────┐
│         job:{jobId}          │
│ ─────────────────────────── │
│ jobId        → String        │
│ jobName      → String        │
│ userId       → String        │
│ createdAt    → Timestamp     │
│ retryPolicy  → JSON          │
│ schedule     → JSON          │
│ status       → String        │
└─────────────────────────────┘
             │
             │
             ▼
┌──────────────────────────────┐
│         dag:{jobId}           │
│ ──────────────────────────── │
│ task_101    → []              │
│ task_102    → ["task_101"]     │
│ task_103    → ["task_102"]     │
└──────────────────────────────┘
             │
 ┌───────────┴───────────┐
 │                       │
 ▼                       ▼
┌──────────────────────────────┐        ┌──────────────────────────────┐
│         task:{taskId}         │        │         task:{taskId}         │
│ ──────────────────────────── │        │ ──────────────────────────── │
│ taskId      → String          │        │ taskId      → String          │
│ taskName    → String          │        │ taskName    → String          │
│ command     → String          │        │ command     → String          │
│ retries     → Number          │        │ retries     → Number          │
│ timeout     → Number          │        │ timeout     → Number          │
│ status      → String          │        │ status      → String          │
│ startTime   → Timestamp       │        │ startTime   → Timestamp       │
│ endTime     → Timestamp       │        │ endTime     → Timestamp       │
└──────────────────────────────┘        └──────────────────────────────┘
 │                        │
 │                        │
 ▼                        ▼
 task:{taskId}:status    task:{taskId}:log

(Separate string keys for fast lookup)
```

---

# 🔵 Dead Letter Queue for Failed Tasks

```
┌──────────────────────────────┐
│        dlq:{jobId} (List)     │
│ ──────────────────────────── │
│ task_105                     │
│ task_110                     │
│ task_118                     │
└──────────────────────────────┘
```

> **Note:** DLQ stores the list of task IDs that failed even after retries.

---

# 📊 Quick Legend:

| Symbol | Meaning                          |
|--------|----------------------------------|
| `job:{jobId}` | Job metadata |
| `dag:{jobId}` | Task dependency graph |
| `task:{taskId}` | Task metadata |
| `task:{taskId}:status` | Task current status |
| `task:{taskId}:log` | Task execution logs |
| `dlq:{jobId}` | List of failed tasks |

---

# ✅ This diagram shows:

- How a **Job** owns a **DAG** and a **collection of Tasks**
- How **Tasks** are independently stored and tracked
- How **status** and **logs** are accessed separately
- How **failures** are captured in a **Dead Letter Queue**

