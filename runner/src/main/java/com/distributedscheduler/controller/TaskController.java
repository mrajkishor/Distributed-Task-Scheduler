package com.distributedscheduler.controller;

import com.distributedscheduler.dto.ErrorResponse;
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
    public ResponseEntity<?> submitTask(@Valid @RequestBody TaskRequest request) {
        try {
            Task task = taskService.createTask(request);
            return ResponseEntity.ok(new TaskResponse(task));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorResponse("🚫 DAG validation failed", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ErrorResponse("❌ Internal Error", e.getMessage())
            );
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @PathVariable String id,
            @RequestHeader("X-Tenant-ID") String tenantId) {
        Task task = taskService.getTaskById(tenantId, id);
        return ResponseEntity.ok(task);
    }
}
