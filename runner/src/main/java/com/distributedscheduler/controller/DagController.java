package com.distributedscheduler.controller;

import com.distributedscheduler.service.dag.DagExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dag")
public class DagController {

    private final DagExecutorService dagExecutorService;

    public DagController(DagExecutorService dagExecutorService) {
        this.dagExecutorService = dagExecutorService;
    }

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')") // <-- Only allow ADMIN role
    public ResponseEntity<?> runDag(@RequestHeader("X-Tenant-ID") String tenantId) {
        dagExecutorService.executeDag(tenantId); // Your execution logics
        return ResponseEntity.ok("✅ DAG execution triggered for tenant: " + tenantId);
    }
}
