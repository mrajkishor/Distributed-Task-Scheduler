package com.distributedscheduler.model;

import com.distributedscheduler.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JobTest {

    @Test
    public void testJobConstructorAndGetters() {
        // Arrange
        Task task1 = new Task("task1", "Task 1", List.of(), TaskStatus.PENDING, "");
        Task task2 = new Task("task2", "Task 2", List.of("task1"), TaskStatus.PENDING, "");
        List<Task> taskList = Arrays.asList(task1, task2);

        RetryPolicy retryPolicy = new RetryPolicy(3, 10);
        Schedule schedule = new Schedule("0 0 * * *", true);
        JobStatus status = JobStatus.PENDING;

        Map<String, String> metadata = new HashMap<>();
        metadata.put("owner", "admin");

        // Act
        Job job = new Job("job1", "Sample Job", taskList, retryPolicy, schedule, status, metadata);

        // Assert
        assertEquals("job1", job.getId());
        assertEquals("Sample Job", job.getName());
        assertEquals(2, job.getTasks().size());
        assertEquals(3, job.getRetryPolicy().getMaxRetries());
        assertTrue(job.getSchedule().isRecurring());
        assertEquals(JobStatus.PENDING, job.getStatus());
        assertEquals("admin", job.getMetadata().get("owner"));
    }

    @Test
    public void testSetters() {
        Job job = new Job();
        job.setId("job2");
        job.setName("Another Job");

        Task task = new Task("t1", "Do work", List.of(), TaskStatus.RUNNING, "Started");
        job.setTasks(List.of(task));

        RetryPolicy policy = new RetryPolicy(5, 20);
        job.setRetryPolicy(policy);

        Schedule schedule = new Schedule("*/5 * * * *", false);
        job.setSchedule(schedule);

        job.setStatus(JobStatus.RUNNING);
        job.setMetadata(Map.of("env", "test"));

        assertEquals("job2", job.getId());
        assertEquals("Another Job", job.getName());
        assertEquals("Do work", job.getTasks().get(0).getName());
        assertEquals(5, job.getRetryPolicy().getMaxRetries());
        assertEquals("*/5 * * * *", job.getSchedule().getCronExpression());
        assertEquals(JobStatus.RUNNING, job.getStatus());
        assertEquals("test", job.getMetadata().get("env"));
    }
}
