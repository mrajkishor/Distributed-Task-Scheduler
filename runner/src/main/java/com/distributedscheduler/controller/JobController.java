package com.distributedscheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    @GetMapping("/ping")
    public String ping() {
        return "Distributed Scheduler is up!";
    }
}
