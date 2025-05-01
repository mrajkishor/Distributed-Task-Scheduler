# 📂 Redis Schema Design – Distributed Task Scheduler

This document defines the **Redis key-value structures** used to store and manage jobs, tasks, DAG dependencies, and runtime metadata for the Distributed Task Scheduler system.

The design ensures **fast lookup**, **efficient updates**, and **scalability** for thousands of concurrent jobs and tasks.

---

## 📋 Key Naming Conventions

| **Key Pattern**                | **Description**                                  |
|-------------------------------|--------------------------------------------------|
| `job:{jobId}`                 | Stores metadata for a Job (DAG + details)        |
| `task:{taskId}`               | Stores metadata for a single Task                |
| `dag:{jobId}:forward`         | Maps task ➝ list of children (Adjacency List)    |
| `dag:{jobId}:reverse`         | Maps task ➝ list of parents (Inverse mapping)    |
| `job:{jobId}:status`          | Stores the current execution status of the job   |
| `task:{taskId}:status`        | Stores the current execution status of the task  |
| `task:{taskId}:log`           | Stores execution logs for a task                 |
| `dlq:{jobId}`                 | Dead Letter Queue for failed tasks in a job      |

---

## 📦 Value Structures

### 📝 1. Job Metadata (`job:{jobId}`)

Stored as a **Redis Hash**.

| Field         | Type    | Description                          |
|---------------|---------|--------------------------------------|
| `jobId`       | String  | Unique Job ID                        |
| `jobName`     | String  | Human-readable job name              |
| `userId`      | String  | User ID if multi-tenant support      |
| `createdAt`   | ISO8601 Timestamp | Job creation time        |
| `retryPolicy` | JSON    | Retry config (maxRetries, backoff)   |
| `schedule`    | JSON    | Scheduled time or recurring config   |
| `status`      | String  | pending / running / failed / completed |

✅ **Example (Hash Fields):**
```bash
HSET job:job_202 jobId "job_202" jobName "DailyReportPipeline" userId "user_1" createdAt "2025-04-20T10:00:00Z" retryPolicy '{"maxRetries":3,"strategy":"exponential"}' schedule '{"cron":"0 0 * * *"}' status "pending"
```

---

### 🧩 2. Task Metadata (`task:{taskId}`)

Stored as a **Redis Hash**.

| Field         | Type    | Description                          |
|---------------|---------|--------------------------------------|
| `taskId`      | String  | Unique Task ID                        |
| `taskName`    | String  | Human-readable task name              |
| `command`     | String  | Command/script to execute             |
| `retries`     | Number  | Retry count remaining                 |
| `timeout`     | Number  | Max execution time (seconds)          |
| `status`      | String  | pending / running / failed / success  |
| `startTime`   | ISO8601 Timestamp | Start time                 |
| `endTime`     | ISO8601 Timestamp | End time                   |

✅ **Example (Hash Fields):**
```bash
HSET task:task_101 taskId "task_101" taskName "SendEmail" command "python send_email.py" retries 3 timeout 60 status "pending"
```

---

### 🔗 3. DAG Structure (`dag:{jobId}:forward`, `dag:{jobId}:reverse`)

Stored as **Redis Hashes** for forward and reverse edges:

- `dag:{jobId}:forward` – task ➝ children
- `dag:{jobId}:reverse` – task ➝ parents

✅ **Example (Hash Fields):**
```bash
HSET dag:job_202:forward task_101 '["task_102"]'
HSET dag:job_202:forward task_102 '["task_103"]'
HSET dag:job_202:forward task_103 '[]'

HSET dag:job_202:reverse task_101 '[]'
HSET dag:job_202:reverse task_102 '["task_101"]'
HSET dag:job_202:reverse task_103 '["task_102"]'
```

📝 **Note:** For better scalability, forward/reverse edges can also be stored using `SADD dag:job_202:forward:{taskId}` and `SADD dag:job_202:reverse:{taskId}`.

---

### 📈 4. Execution Status (`task:{taskId}:status`, `job:{jobId}:status`)

Simple **String** keys for quick status checks.

✅ **Example:**
```bash
SET task:task_101:status "completed"
SET job:job_202:status "running"
```

---

### 📝 5. Execution Logs (`task:{taskId}:log`)

Simple **String** or **List** (if you want multiple log lines).

✅ **Example:**
```bash
SET task:task_101:log "Task executed successfully. Email sent to 300 users."
```

Or:
```bash
LPUSH task:task_101:log "Starting execution" "Sending email" "Execution completed successfully"
```

---

### ⚰️ 6. Dead Letter Queue (`dlq:{jobId}`)

Stores failed task IDs for a particular job.

✅ **Example (Redis List):**
```bash
RPUSH dlq:job_202 task_105 task_110
```

---

## 🏗️ Access Patterns

| **Action**                     | **Redis Operation**               |
|--------------------------------|-----------------------------------|
| Submit a new Job              | HMSET job:{jobId}                 |
| Add Tasks and Dependencies    | HMSET task:{taskId}, HSET dag:{jobId} |
| Update Task Status            | SET task:{taskId}:status          |
| Query Task Logs               | GET / LRANGE task:{taskId}:log    |
| List Failed Tasks             | LRANGE dlq:{jobId} 0 -1           |

---

## ⚙️ Redis Key Structure Summary

```
job:{jobId}              → Job metadata (Hash)
task:{taskId}            → Task metadata (Hash)
dag:{jobId}:forward      → Task ➝ Children mapping (Hash)
dag:{jobId}:reverse      → Task ➝ Parent mapping (Hash)
job:{jobId}:status       → Job execution status (String)
task:{taskId}:status     → Task execution status (String)
task:{taskId}:log        → Execution logs (String or List)
dlq:{jobId}              → Dead letter queue (List)
```

---

## 🧠 DAG Traversal Logic

### ✅ Check if task is ready to execute:
```
parents = HGET dag:job_202:reverse task_102
for each parent in parents:
    if GET task:{parent}:status != "completed":
        return false
return true
```

### 📤 Trigger children after task completion:
```
children = HGET dag:job_202:forward task_101
for each child in children:
    if all parents of child are completed:
        enqueue(child)
```

---

## 🖼️ DAG Schema Diagram (Textual)

```
               ┌────────────┐
               │ task_101   │
               └────┬───────┘
                    │
       ┌────────────▼────────────┐
       │  dag:job_202:forward    │
       │  task_101 ➝ [task_102]  │
       └─────────────────────────┘
                    │
               ┌────▼─────┐
               │ task_102 │
               └────┬─────┘
                    │
       ┌────────────▼────────────┐
       │  dag:job_202:forward    │
       │  task_102 ➝ [task_103]  │
       └─────────────────────────┘
                    │
               ┌────▼─────┐
               │ task_103 │
               └──────────┘
```

---

## ✅ Summary

- Fast DAG traversal using forward/reverse mappings
- Efficient parent-checks before task execution
- Scalable key structure for large distributed workflows
- Flexible and ready for DAG executor integration
