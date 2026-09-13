package com.wellness360.taskmanager.service;

import com.wellness360.taskmanager.dto.TaskRequestDto;
import com.wellness360.taskmanager.dto.TaskResponseDto;
import com.wellness360.taskmanager.model.TaskStatus;

import java.util.List;

public interface TaskService {

    List<TaskResponseDto> getAllTasks(TaskStatus status);

    TaskResponseDto getTaskById(Long id);

    TaskResponseDto createTask(TaskRequestDto requestDto);

    TaskResponseDto updateTask(Long id, TaskRequestDto requestDto);

    void deleteTask(Long id);

    TaskResponseDto markTaskAsComplete(Long id);
}
