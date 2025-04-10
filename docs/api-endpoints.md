# 📘 API Endpoints – Distributed Task Scheduler

This document lists all the REST API endpoints designed to support the core functionality of the Distributed Task Scheduler system, including job creation, DAG-based task management, execution handling, and future scalability.

---

## 🔹 1. Core Job Management Endpoints

### `POST /jobs`
Create a new job.
- Request: JSON payload with job details and tasks
- Response: Job ID and success message

### `GET /jobs`
Get all jobs.
- Response: Array of job summaries

### `GET /jobs/{id}`
Get details of a specific job.
- Response: Job with tasks, DAG, schedule, and metadata

### `PUT /jobs/{id}`
Update job information.
- Request: Partial or full job object
- Response: Success message

### `DELETE /jobs/{id}`
Delete a job.
- Response: Success message

---

## 🔹 2. DAG/Task Management Endpoints

### `POST /jobs/{id}/tasks`
Add a new task to an existing job.
- Request: Task object with `id`, `name`, and `dependencies`
- Response: Success message

### `GET /jobs/{id}/tasks`
Get all tasks in a job.
- Response: List of tasks with dependency info

### `PUT /jobs/{id}/tasks/{taskId}`
Update a task in a job.
- Request: Updated task object
- Response: Success message

### `DELETE /jobs/{id}/tasks/{taskId}`
Delete a specific task from a job.
- Response: Success message

---

## 🔹 3. Execution-Related Endpoints

### `POST /jobs/{id}/trigger`
Manually trigger job execution.
- Response: Execution ID and confirmation message

### `GET /jobs/{id}/status`
Get the latest execution status of a job.
- Response: Job execution status (`PENDING`, `RUNNING`, etc.)

### `GET /jobs/{id}/history`
Get the execution history of a job.
- Response: List of execution records with timestamps and status

---

## 🔹 4. Scalability (Planned Features)

### (Planned) `POST /jobs/{id}/pause`
Pause a job's execution or scheduling.

### (Planned) `POST /jobs/{id}/resume`
Resume a paused job.

### `retryPolicy` in job schema
Supports `NONE`, `ON_FAILURE`, `ALWAYS` with optional backoff and retry count.

### `schedule` field (cron syntax)
Supports scheduled/recurring jobs using cron expressions.

---

## ✅ Deliverable

All of the above are documented in `openapi.yaml` and this file. For future enhancements like pause/resume, implementation will follow as a separate phase.
