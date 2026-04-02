package com.skillstorm.taskmanager.DTOs;

import java.time.LocalDateTime;

import com.skillstorm.taskmanager.Enums.Priority;
import com.skillstorm.taskmanager.Enums.Status;

public class TaskRequestDTO {

    private Long assignedUserId;
    private Long assignedTeamId;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDateTime dueDate;

    public TaskRequestDTO() {
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Long getAssignedTeamId() {
        return assignedTeamId;
    }

    public void setAssignedTeamId(Long assignedTeamId) {
        this.assignedTeamId = assignedTeamId;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
