# 📦 Redis Key Design – DAG Job Scheduler (Issue #39)

This document outlines the key naming conventions and storage formats used in Redis for the distributed task scheduler system. These conventions improve clarity, scalability, and query efficiency.

---

## 🔑 Key Naming Conventions

| Entity       | Key Format                            | Type     | Description                                  |
|--------------|----------------------------------------|----------|----------------------------------------------|
| Job Metadata | `job:{jobId}`                          | Hash     | Stores job details like status, owner        |
| Task         | `task:{taskId}`                        | Hash     | Stores task info (status, retries, etc.)     |
| Task Logs    | `task:{taskId}:logs`                   | List     | Execution log messages for a task            |
| Task Status  | `task:{taskId}:status`                 | String   | Stores current task status                   |
| DAG Edges    | `dag:{jobId}:edges`                    | Hash     | Maps taskId -> list of dependency IDs        |
| DAG Roots    | `dag:{jobId}:roots`                    | Set      | Tasks with no dependencies (starting points) |
| DAG Visited  | `dag:{jobId}:visited`                  | Set      | Used for topological traversal runtime       |

---

## 🧾 Example Key-Value Structures

### 🎯 Job Metadata
**Key**: `job:invoice-2024-123`  
**Type**: Hash

```json
{
  "jobId": "invoice-2024-123",
  "createdBy": "user-456",
  "status": "PENDING",
  "createdAt": "2024-04-16T10:00:00Z"
}
```

---

### 🔧 Task Metadata
**Key**: `task:email-task-1`  
**Type**: Hash

```json
{
  "id": "email-task-1",
  "jobId": "invoice-2024-123",
  "name": "Send Invoice Email",
  "status": "RUNNING",
  "retryCount": 1,
  "maxRetries": 3
}
```

**Logs**:  
**Key**: `task:email-task-1:logs`  
**Type**: List

```plaintext
2024-04-16T11:20Z - Retry attempt 1 failed due to timeout
2024-04-16T11:25Z - Retry attempt 2 started
```

---

### 🔗 DAG Structure
**Key**: `dag:invoice-2024-123:edges`  
**Type**: Hash  
**Example**:

```bash
HSET dag:invoice-2024-123:edges task-a ""
HSET dag:invoice-2024-123:edges task-b "task-a"
HSET dag:invoice-2024-123:edges task-c "task-a,task-b"
```

**Key**: `dag:invoice-2024-123:roots`  
**Type**: Set  
```bash
SADD dag:invoice-2024-123:roots task-a
```

---

## ♻️ Cleanup and TTL Strategy

Use `EXPIRE` to auto-delete old data:
```bash
EXPIRE job:invoice-2024-123 86400
EXPIRE task:email-task-1 86400
EXPIRE dag:invoice-2024-123:edges 86400
```

---

## ✅ Summary

These Redis keys support scalable job scheduling, DAG traversal, retry handling, and real-time monitoring. They are compatible with both atomic updates and future horizontal scaling strategies.
