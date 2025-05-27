package com.distributedscheduler.controller;

import com.distributedscheduler.dto.TaskRequest;
import com.distributedscheduler.model.Task;
import com.distributedscheduler.model.TaskStatus;
import com.distributedscheduler.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSubmitTask_successful() throws Exception {
        // Prepare mock request
        TaskRequest request = new TaskRequest();
        request.setName("generate-report");
        request.setPayload(Map.of("type", "pdf", "data", "test"));
        request.setPriority(5);
        request.setDelaySeconds(10);
        request.setMaxRetries(3);

        // Mocked Task return from service
        Task mockTask = new Task();
        mockTask.setId(UUID.randomUUID().toString());
        mockTask.setName(request.getName());
        mockTask.setPayload(request.getPayload());
        mockTask.setPriority(request.getPriority());
        mockTask.setDelaySeconds(request.getDelaySeconds());
        mockTask.setStatus(TaskStatus.PENDING);
        mockTask.setMaxRetries(request.getMaxRetries());

        Mockito.when(taskService.createTask(Mockito.any(TaskRequest.class))).thenReturn(mockTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.name").value("generate-report"));
    }

    @Test
    public void testSubmitTask_invalidRequest() throws Exception {
        TaskRequest invalidRequest = new TaskRequest(); // name is missing

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
