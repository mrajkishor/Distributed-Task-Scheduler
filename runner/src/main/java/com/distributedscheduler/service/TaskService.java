    package com.distributedscheduler.service;

    import com.distributedscheduler.model.Task;
    import com.distributedscheduler.dto.TaskRequest;

    import java.util.List;

    /**
     * Service interface for managing tasks within the Distributed Task Scheduler.
     * Defines operations for task creation and future extensions like status updates.
     */
    public interface TaskService {

        /**
         * Creates a new task based on the given request.
         *
         * @param request The TaskRequest object containing task details.
         * @return The created Task object with generated ID and initial status.
         */
        Task createTask(TaskRequest request);


        Task getTaskById(String tenantId, String taskId);

        void addDependenciesByName(String tenantId, String taskName, List<String> dependsOn);



    }


    /**
     * About this component
     *
     *
     * This `TaskService` interface defines the **core operations** for managing tasks in your Distributed Task Scheduler system.
     *
     * ---
     *
     * ### ✅ **Key Responsibilities:**
     *
     * #### 1. `Task createTask(TaskRequest request)`
     *
     * * Creates a new task from the provided `TaskRequest`.
     * * Likely involves:
     *
     *   * Generating a unique ID
     *   * Setting initial status (e.g., `PENDING`)
     *   * Saving it to Redis or DB
     *   * Possibly checking for idempotency
     *
     * ---
     *
     * #### 2. `Task getTaskById(String tenantId, String taskId)`
     *
     * * Retrieves a task by tenant ID and task ID.
     * * Used to check task status, view details, etc.
     *
     * ---
     *
     * #### 3. `void addDependenciesByName(String tenantId, String taskName, List<String> dependsOn)`
     *
     * * Adds named dependencies to a task.
     * * Used to build a **DAG** (Directed Acyclic Graph) of tasks.
     * * Example: `Task B depends on A → B runs only after A completes.`
     *
     * ---
     *
     * ### 🧠 Summary:
     *
     * This interface is the **contract** for your task operations like creation, retrieval, and dependency management. It hides implementation details and allows you to plug in different service implementations behind a common API.
     *
     * **/