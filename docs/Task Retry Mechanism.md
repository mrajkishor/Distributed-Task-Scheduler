
## 🔁 Task Retry Mechanism – Explained for Users

When a task is submitted, your system tries to **execute it**. But sometimes, things can go wrong — like a missing input, temporary service failure, or a network timeout. This is where the **retry mechanism** comes in.

---

### ✅ When Will the System Retry a Task?

The system automatically retries a task **if it fails during execution**, regardless of whether the payload is present or not.

### Common Failure Scenarios That Trigger Retry:

* The task has no required input data (empty `payload`)
* A network issue (e.g., no internet or service timeout)
* The backend service the task depends on is down (HTTP 500)
* Some logic inside the task code crashes unexpectedly

---

### 🔁 How Many Times Will It Retry?

Each task has a `maxRetries` setting. If you set `maxRetries: 3`, the system will try to execute the task:

| Attempt | Delay Before Retry | What Happens            |
| ------- | ------------------ | ----------------------- |
| 1st     | Immediate          | First attempt           |
| 2nd     | 2 seconds later    | Retry after 2s          |
| 3rd     | 4 seconds later    | Retry again             |
| 4th     | 8 seconds later    | Final retry             |
| After   | —                  | Task is marked `FAILED` |

> 🔹 The delay increases exponentially: `2s`, `4s`, `8s`...

---

### 🧠 Example:

You submitted a task with valid payload:

```json
{
  "name": "process-order",
  "payload": { "orderId": "12345" },
  "maxRetries": 3
}
```

But during execution, the system tries to call another service (e.g., payment API), and that service fails due to a **network error**.

✅ The system will **automatically retry** this task up to 3 times.
⛔ If it still fails, the task status becomes `FAILED`.

---

### 📍 Why Is This Useful?

Without retry:

* A temporary issue would make your task fail permanently

With retry:

* The system waits and tries again automatically
* You avoid manual resubmission
* Reliability improves

---

### ✅ Final Statuses You Could See

| Status      | Meaning                              |
| ----------- | ------------------------------------ |
| `COMPLETED` | Task ran successfully                |
| `RETRYING`  | Currently waiting for the next retry |
| `FAILED`    | All retries failed                   |

