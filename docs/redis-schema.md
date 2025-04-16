# 🧠 Redis Schema for Tasks

## 🔑 Key Format
```
task:{taskId}
```

## 🧾 Value (JSON)

```json
{
  "id": "email-task-123",
  "name": "Send Welcome Email",
  "dependencies": ["fetch-user-data"],
  "status": "PENDING",
  "retryCount": 0,
  "maxRetries": 3,
  "log": "Created at 2024-04-16T10:00:00Z",
  "executionLogs": [
    "Created at 2024-04-16T10:00:00Z"
  ]
}
```

> Stored as JSON string using Redis `SET` or `HSET`.
