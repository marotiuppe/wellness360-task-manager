package com.wellness360.taskmanager.service.impl;

import com.wellness360.taskmanager.dto.TaskRequestDto;
import com.wellness360.taskmanager.dto.TaskResponseDto;
import com.wellness360.taskmanager.exception.ResourceNotFoundException;
import com.wellness360.taskmanager.model.Task;
import com.wellness360.taskmanager.model.TaskStatus;
import com.wellness360.taskmanager.repository.TaskRepository;
import com.wellness360.taskmanager.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks(TaskStatus status) {
        String currentUsername = getCurrentUsername();
        boolean isAdmin = isAdminUser();

        List<Task> tasks;
        if (isAdmin || currentUsername == null) {
            tasks = (status != null) ? taskRepository.findByStatus(status) : taskRepository.findAll();
        } else {
            tasks = (status != null) 
                    ? taskRepository.findByOwnerAndStatus(currentUsername, status)
                    : taskRepository.findByOwner(currentUsername);
        }

        return tasks.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = findEntityById(id);
        validateTaskAccess(task);
        return mapToResponseDto(task);
    }

    @Override
    @Transactional
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        String currentUsername = getCurrentUsername();
        TaskStatus status = (requestDto.getStatus() != null) ? requestDto.getStatus() : TaskStatus.PENDING;

        Task task = Task.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .dueDate(requestDto.getDueDate())
                .status(status)
                .owner(currentUsername != null ? currentUsername : "Maroti Uppe")
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponseDto(savedTask);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto requestDto) {
        Task existingTask = findEntityById(id);
        validateTaskAccess(existingTask);

        existingTask.setTitle(requestDto.getTitle());
        existingTask.setDescription(requestDto.getDescription());
        existingTask.setDueDate(requestDto.getDueDate());
        if (requestDto.getStatus() != null) {
            existingTask.setStatus(requestDto.getStatus());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return mapToResponseDto(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = findEntityById(id);
        validateTaskAccess(task);
        taskRepository.delete(task);
    }

    @Override
    @Transactional
    public TaskResponseDto markTaskAsComplete(Long id) {
        Task task = findEntityById(id);
        validateTaskAccess(task);
        task.setStatus(TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(task);
        return mapToResponseDto(updatedTask);
    }

    private Task findEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }

    private boolean isAdminUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
        }
        return false;
    }

    private void validateTaskAccess(Task task) {
        String currentUsername = getCurrentUsername();
        if (currentUsername != null && !isAdminUser() && task.getOwner() != null && !task.getOwner().equals(currentUsername)) {
            throw new ResourceNotFoundException("Task not found with ID: " + task.getId());
        }
    }

    private TaskResponseDto mapToResponseDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .owner(task.getOwner())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
