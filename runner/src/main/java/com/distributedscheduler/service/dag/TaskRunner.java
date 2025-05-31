package com.distributedscheduler.service.dag;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.repository.TaskRedisRepository;
import com.distributedscheduler.lock.RedisLockService;
import org.springframework.stereotype.Service;

@Service
public class TaskRunner {

    private final TaskRedisRepository taskRepository;
    private final RedisLockService lockService;

    public TaskRunner(TaskRedisRepository taskRepository, RedisLockService lockService) {
        this.taskRepository = taskRepository;
        this.lockService = lockService;
    }

    public void run(Task task) {
        String taskId = task.getId();
        String tenantId = task.getTenantId();

        if (!lockService.acquireLock(taskId, 60000)) {
            System.out.println("🔒 Task already locked or in progress: " + taskId);
            return;
        }

        try {
            if (task.getRetryCount() >= task.getMaxRetries()) {
                System.out.println("🚫 Max retries reached. Moving to DLQ: " + taskId);
                task.setStatus(TaskStatus.DLQ);
                taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.DLQ);
                return;
            }

            System.out.println("🚀 Executing task: " + taskId);
            task.setStatus(TaskStatus.RETRYING);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.RETRYING);

            Thread.sleep(300); // simulate processing time

            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.COMPLETED);
            System.out.println("✅ Task succeeded: " + taskId);

        } catch (Exception e) {
            System.out.println("❌ Task execution failed: " + taskId);
            task.setRetryCount(task.getRetryCount() + 1);
            task.setStatus(TaskStatus.FAILED);
            taskRepository.updateTaskStatus(tenantId, taskId, TaskStatus.FAILED);
        } finally {
            lockService.releaseLock(taskId);
        }
    }
}

/**
 *
 * The `TaskRunner` component is responsible for **executing a single task safely** by:
 *
 * ---
 *
 * ### ✅ **Purpose:**
 *
 * 1. **Preventing duplicate execution** using a Redis-based lock.
 * 2. **Handling task status updates** (`RETRYING`, `COMPLETED`, `FAILED`, `DLQ`).
 * 3. **Respecting max retry limits.**
 *
 * ---
 *
 * ### ⚙️ **How it works:**
 *
 * #### `run(Task task)` flow:
 *
 * 1. **Locking**:
 *
 *    * Tries to acquire a Redis lock for the task ID (for 60 seconds).
 *    * If lock fails → task is **already in progress**, so it skips execution.
 *
 * 2. **Retry limit check**:
 *
 *    * If `retryCount >= maxRetries` → moves task to `DLQ` (Dead Letter Queue) and exits.
 *
 * 3. **Execution simulation**:
 *
 *    * Marks task `RETRYING`.
 *    * Sleeps 300ms to simulate processing.
 *    * Marks task `COMPLETED`.
 *
 * 4. **Failure handling**:
 *
 *    * On exception: increments `retryCount`, sets status to `FAILED`.
 *
 * 5. **Unlocking**:
 *
 *    * Releases Redis lock regardless of success/failure (in `finally` block).
 *
 * ---
 *
 * ### 🧠 Summary:
 *
 * 🔁 `TaskRunner` is a **low-level executor** that ensures a task:
 *
 * * Runs once at a time,
 * * Honors retry limits,
 * * Updates status persistently in Redis,
 * * And gracefully handles failures or race conditions.
 *
 * **/



/**
 * Q. Is Retryhandler and TaskRunner duplicates. or they have diffent purpose.. its used both in project?
 *
 *Ans:
 *
 * They are **not duplicates** — they have **different purposes** and **serve different execution flows** in your project.
 *
 * ---
 *
 * ### 🔁 `RetryHandler` — *Used in Delay Queue Poller (background queue retry engine)*
 *
 * * Handles **automatic retries** when tasks fail due to exceptions.
 * * Applies **exponential backoff** and schedules retry via Redis Delay Queue.
 * * Delegates actual task execution to `TaskExecutor`.
 * * Used in: `DelayQueuePoller`.
 *
 * ---
 *
 * ### ⚙️ `TaskRunner` — *Used in DAG Executor (explicit DAG-based execution)*
 *
 * * Executes **DAG tasks** manually or via dependency resolution.
 * * Uses **basic retry check** and locks to ensure task safety.
 * * Suitable when tasks are not coming from retry queue but rather scheduled DAG order.
 * * Used in: `DagExecutorService`.
 *
 * ---
 *
 * ### 🧠 Key Insight:
 *
 * | Aspect               | `RetryHandler`              | `TaskRunner`                                |
 * | -------------------- | --------------------------- | ------------------------------------------- |
 * | Retry Backoff Logic  | ✅ Yes (exponential)         | ❌ No (immediate execution or DLQ)           |
 * | Use Case             | Delay queue retry mechanism | DAG topological execution                   |
 * | Lock Handling        | ❌ Assumes caller handles it | ✅ Acquires and releases lock                |
 * | Dependency Awareness | ❌ Not aware of dependencies | ✅ Used in DAG, checks dependency completion |
 * | Execution Logic      | Delegates to `TaskExecutor` | In-line simulated logic (can be extended)   |
 *
 * ---
 *
 * ### ✅ Conclusion:
 *
 * They **complement** each other, not duplicate:
 *
 * * Use `TaskRunner` in DAG mode (task chaining based on dependency).
 * * Use `RetryHandler` for automatic retries of failed tasks from queue.
 *
 * You need **both** in a distributed scheduler with DAG + retry capabilities.
 *
 *
 * **/