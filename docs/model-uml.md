+----------------------+
|        Job           |
+----------------------+
| - id: String         |
| - name: String       |
| - tasks: List<Task>  |
| - retryPolicy: RetryPolicy |
| - schedule: Schedule |
| - status: JobStatus  |
| - metadata: Map<String, String> |
+----------------------+
| +getters/setters     |
+----------------------+
|
| contains
v
+----------------------+
|        Task          |
+----------------------+
| - id: String         |
| - name: String       |
| - dependencies: List<String> |
| - status: TaskStatus |
| - log: String        |
+----------------------+
| +getters/setters     |
+----------------------+

+----------------------+
|    RetryPolicy       |
+----------------------+
| - maxRetries: int    |
| - delaySeconds: int  |
+----------------------+
| +getters/setters     |
+----------------------+

+----------------------+
|      Schedule        |
+----------------------+
| - cronExpression: String  |
| - isRecurring: boolean    |
+----------------------+
| +getters/setters     |
+----------------------+

+----------------------+
|     JobStatus        |
+----------------------+
| PENDING              |
| RUNNING              |
| COMPLETED            |
| FAILED               |
+----------------------+

+----------------------+
|    TaskStatus        |
+----------------------+
| PENDING              |
| RUNNING              |
| COMPLETED            |
| FAILED               |
| SKIPPED              |
+----------------------+
