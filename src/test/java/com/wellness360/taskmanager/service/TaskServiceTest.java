package com.wellness360.taskmanager.service;

import com.wellness360.taskmanager.dto.TaskRequestDto;
import com.wellness360.taskmanager.dto.TaskResponseDto;
import com.wellness360.taskmanager.exception.ResourceNotFoundException;
import com.wellness360.taskmanager.model.Task;
import com.wellness360.taskmanager.model.TaskStatus;
import com.wellness360.taskmanager.repository.TaskRepository;
import com.wellness360.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private TaskRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Complete Wellness360 Assignment")
                .description("Build Spring Boot REST API for task management")
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = TaskRequestDto.builder()
                .title("Complete Wellness360 Assignment")
                .description("Build Spring Boot REST API for task management")
                .dueDate(LocalDate.now().plusDays(1))
                .status(TaskStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a task")
    void createTask_Success() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDto result = taskService.createTask(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Complete Wellness360 Assignment");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should retrieve a task by ID")
    void getTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponseDto result = taskService.getTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(sampleTask.getTitle());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID does not exist")
    void getTaskById_NotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Task not found with ID: 99");
    }

    @Test
    @DisplayName("Should mark task status as completed")
    void markTaskAsComplete_Success() {
        Task completedTask = Task.builder()
                .id(1L)
                .title(sampleTask.getTitle())
                .description(sampleTask.getDescription())
                .status(TaskStatus.COMPLETED)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(completedTask);

        TaskResponseDto result = taskService.markTaskAsComplete(1L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should return all tasks")
    void getAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponseDto> results = taskService.getAllTasks(null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo(sampleTask.getTitle());
    }

    @Test
    @DisplayName("Should delete task by ID")
    void deleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.deleteTask(1L);

        verify(taskRepository).delete(sampleTask);
    }
}
