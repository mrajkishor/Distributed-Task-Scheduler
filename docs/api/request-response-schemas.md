# 📦 Request and Response Schemas – Distributed Task Scheduler

This document defines the request and response structures for all REST API endpoints.

---

## ✅ 1. Job Management Endpoints

### `POST /jobs` – Create Job
**Request:**
```json
{
  "name": "Daily Report Job",
  "tasks": [
    {
      "id": "task1",
      "name": "Extract Data",
      "dependencies": []
    },
    {
      "id": "task2",
      "name": "Send Email",
      "dependencies": ["task1"]
    }
  ],
  "retryPolicy": {
    "strategy": "ON_FAILURE",
    "maxRetries": 3,
    "backoff": "FIXED",
    "retryDelayMs": 1000
  },
  "schedule": "cron(0 12 * * ? *)"
}
```

**Response:**
```json
{
  "jobId": "abc123",
  "message": "Job created successfully"
}
```

---

### `GET /jobs` – List Jobs
**Response:**
```json
[
  {
    "jobId": "abc123",
    "name": "Daily Report Job",
    "status": "SCHEDULED"
  }
]
```

---

### `GET /jobs/{id}` – Get Job Details
**Response:**
```json
{
  "jobId": "abc123",
  "name": "Daily Report Job",
  "status": "SCHEDULED",
  "schedule": "cron(0 12 * * ? *)",
  "retryPolicy": {
    "strategy": "ON_FAILURE",
    "maxRetries": 3
  },
  "tasks": [
    {
      "id": "task1",
      "name": "Extract Data",
      "dependencies": [],
      "status": "COMPLETED"
    }
  ]
}
```

---

### `PUT /jobs/{id}` – Update Job
**Request:**
```json
{
  "name": "Updated Report Job",
  "schedule": "cron(0 18 * * ? *)"
}
```

**Response:**
```json
{
  "message": "Job updated successfully"
}
```

---

### `DELETE /jobs/{id}` – Delete Job
**Response:**
```json
{
  "message": "Job deleted successfully"
}
```

---

## ✅ 2. DAG / Task Endpoints

### `POST /jobs/{id}/tasks`
**Request:**
```json
{
  "id": "task3",
  "name": "Upload to S3",
  "dependencies": ["task1", "task2"]
}
```

**Response:**
```json
{
  "message": "Task added successfully"
}
```

---

### `GET /jobs/{id}/tasks`
**Response:**
```json
[
  {
    "id": "task1",
    "name": "Extract",
    "dependencies": []
  },
  {
    "id": "task2",
    "name": "Send Email",
    "dependencies": ["task1"]
  }
]
```

---

### `PUT /jobs/{id}/tasks/{taskId}`
**Request:**
```json
{
  "name": "Email Customers",
  "dependencies": ["task1"]
}
```

**Response:**
```json
{
  "message": "Task updated successfully"
}
```

---

### `DELETE /jobs/{id}/tasks/{taskId}`
**Response:**
```json
{
  "message": "Task deleted successfully"
}
```

---

## ✅ 3. Execution Endpoints

### `POST /jobs/{id}/trigger`
**Response:**
```json
{
  "message": "Execution started",
  "executionId": "exec123"
}
```

---

### `GET /jobs/{id}/status`
**Response:**
```json
{
  "status": "RUNNING",
  "updatedAt": "2024-10-11T13:00:00Z"
}
```

---

### `GET /jobs/{id}/history`
**Response:**
```json
[
  {
    "executionId": "exec123",
    "status": "COMPLETED",
    "startedAt": "2024-10-11T12:00:00Z",
    "finishedAt": "2024-10-11T12:10:00Z",
    "logs": "Execution completed successfully"
  }
]
```
