package com.distributedscheduler.controller;

import com.distributedscheduler.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.dto.TaskResponse;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Handles submission of a new task.
     *
     * @param request TaskRequest payload
     * @return 200 OK with TaskResponse
     */
    @PostMapping
    public ResponseEntity<TaskResponse> submitTask(@Valid @RequestBody TaskRequest request) {
        Task task = taskService.createTask(request);

        TaskResponse response = new TaskResponse(task);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable String id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        Task task = taskService.getTaskById(tenantId, id);
        return ResponseEntity.ok(task);
    }
}
