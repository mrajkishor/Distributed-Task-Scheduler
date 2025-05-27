
package com.distributedscheduler.api;

import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPostAndGetTask() throws Exception {
        Task newTask = new Task();
        newTask.setId("task123");
        newTask.setName("Sample Task");
        newTask.setTenantId("demo");
        newTask.setPayload(Map.of("message", "Hello"));
        newTask.setStatus(TaskStatus.PENDING);
        newTask.setPriority(5);
        newTask.setDelaySeconds(0);

        // POST /tasks
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task123"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // GET /tasks/{id}
        mockMvc.perform(get("/tasks/demo/task123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("task123"))
                .andExpect(jsonPath("$.name").value("Sample Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
