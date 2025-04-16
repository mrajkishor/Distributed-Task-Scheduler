package com.distributedscheduler.consumer;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskExecutorTest {

    @Test
    public void testRetryAndFailLogic() {
        Task task = new Task();
        task.setId("t1");
        task.setName("Failing Task");
        task.setMaxRetries(1); // simulate immediate failure

        TaskExecutor executor = new TaskExecutor();
        executor.processTask(task);

        assertEquals(TaskStatus.FAILED, task.getStatus());
        assertEquals(1, task.getRetryCount());
        assertFalse(task.getExecutionLogs().isEmpty());
    }

    @Test
    public void testSuccessfulExecution() {
        Task task = new Task();
        task.setId("t2");
        task.setName("Test Task");
        task.setMaxRetries(3);

        TaskExecutor executor = new TaskExecutor() {
//            @Override
//            protected void simulateTaskExecution(Task task) {
//                // force success
//            }
        };

        executor.processTask(task);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(0, task.getRetryCount());
    }
}
