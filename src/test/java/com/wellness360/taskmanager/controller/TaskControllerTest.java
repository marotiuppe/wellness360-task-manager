package com.wellness360.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellness360.taskmanager.dto.TaskRequestDto;
import com.wellness360.taskmanager.dto.TaskResponseDto;
import com.wellness360.taskmanager.exception.ResourceNotFoundException;
import com.wellness360.taskmanager.model.TaskStatus;
import com.wellness360.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private com.wellness360.taskmanager.security.JwtUtils jwtUtils;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private TaskResponseDto sampleResponse;
    private TaskRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = TaskResponseDto.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(2))
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = TaskRequestDto.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(2))
                .status(TaskStatus.PENDING)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /tasks - Should return list of tasks with 200 OK")
    void getAllTasks_Returns200() throws Exception {
        when(taskService.getAllTasks(null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /tasks/{id} - Should return task details with 200 OK")
    void getTaskById_Returns200() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /tasks/{id} - Should return 404 NOT FOUND when task missing")
    void getTaskById_Returns404() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new ResourceNotFoundException("Task not found with ID: 99"));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task not found with ID: 99"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /tasks - Should create task and return 201 CREATED with Location header")
    void createTask_Returns201() throws Exception {
        when(taskService.createTask(any(TaskRequestDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /tasks - Should return 400 BAD REQUEST when title is blank")
    void createTask_ValidationFailure() throws Exception {
        TaskRequestDto invalidRequest = TaskRequestDto.builder()
                .title("")
                .description("Missing title")
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validation_errors.title").value("Task title is required"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /tasks/{id} - Should update task and return 200 OK")
    void updateTask_Returns200() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequestDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /tasks/{id} - Should delete task and return 204 NO CONTENT")
    void deleteTask_Returns204() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /tasks/{id}/complete - Should mark task complete and return 200 OK")
    void markTaskAsComplete_Returns200() throws Exception {
        TaskResponseDto completedResponse = TaskResponseDto.builder()
                .id(1L)
                .title("Test Task")
                .status(TaskStatus.COMPLETED)
                .build();

        when(taskService.markTaskAsComplete(1L)).thenReturn(completedResponse);

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("completed"));
    }
}
