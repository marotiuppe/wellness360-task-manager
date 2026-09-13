package com.wellness360.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    PENDING("pending"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TaskStatus fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (TaskStatus status : TaskStatus.values()) {
            if (status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid task status: " + value + ". Allowed values: pending, in_progress, completed");
    }
}
