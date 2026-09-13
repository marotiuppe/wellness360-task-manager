package com.wellness360.taskmanager.controller;

import com.wellness360.taskmanager.dto.TaskRequestDto;
import com.wellness360.taskmanager.dto.TaskResponseDto;
import com.wellness360.taskmanager.model.TaskStatus;
import com.wellness360.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Task Management API", description = "Endpoints for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all tasks", description = "Fetch all tasks, optionally filtered by status")
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        List<TaskResponseDto> tasks = taskService.getAllTasks(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a specific task by ID")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto requestDto) {
        TaskResponseDto createdTask = taskService.createTask(requestDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto requestDto) {
        TaskResponseDto updatedTask = taskService.updateTask(id, requestDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task by ID")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a task as complete")
    public ResponseEntity<TaskResponseDto> markTaskAsComplete(@PathVariable Long id) {
        TaskResponseDto completedTask = taskService.markTaskAsComplete(id);
        return ResponseEntity.ok(completedTask);
    }
}
