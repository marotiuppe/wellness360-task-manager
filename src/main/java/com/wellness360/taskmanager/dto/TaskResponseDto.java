package com.wellness360.taskmanager.dto;

import com.wellness360.taskmanager.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponseDto {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private String owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponseDto() {
    }

    public TaskResponseDto(Long id, String title, String description, LocalDate dueDate, TaskStatus status, String owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static TaskResponseDtoBuilder builder() {
        return new TaskResponseDtoBuilder();
    }

    public static class TaskResponseDtoBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDate dueDate;
        private TaskStatus status;
        private String owner;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public TaskResponseDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TaskResponseDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TaskResponseDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public TaskResponseDtoBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public TaskResponseDtoBuilder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskResponseDtoBuilder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public TaskResponseDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TaskResponseDtoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TaskResponseDto build() {
            return new TaskResponseDto(id, title, description, dueDate, status, owner, createdAt, updatedAt);
        }
    }
}
