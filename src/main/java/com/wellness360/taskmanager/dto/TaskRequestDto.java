package com.wellness360.taskmanager.dto;

import com.wellness360.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskRequestDto {

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    public TaskRequestDto() {
    }

    public TaskRequestDto(String title, String description, LocalDate dueDate, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public static TaskRequestDtoBuilder builder() {
        return new TaskRequestDtoBuilder();
    }

    public static class TaskRequestDtoBuilder {
        private String title;
        private String description;
        private LocalDate dueDate;
        private TaskStatus status;

        public TaskRequestDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TaskRequestDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TaskRequestDtoBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public TaskRequestDtoBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskRequestDto build() {
            return new TaskRequestDto(title, description, dueDate, status);
        }
    }
}
