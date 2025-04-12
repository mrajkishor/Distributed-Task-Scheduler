
package com.distributedscheduler.model;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testTaskSettersAndGetters() {
        Task task = new Task();
        task.setId("task123");
        task.setName("Sample Task");
        task.setDependencies(List.of("taskA"));
        task.setStatus(TaskStatus.FAILED);
        task.setLog("Execution failed");

        assertEquals("task123", task.getId());
        assertEquals("Sample Task", task.getName());
        assertEquals("taskA", task.getDependencies().get(0));
        assertEquals(TaskStatus.FAILED, task.getStatus());
        assertEquals("Execution failed", task.getLog());
    }
}
