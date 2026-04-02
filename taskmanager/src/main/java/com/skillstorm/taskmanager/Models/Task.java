package com.skillstorm.taskmanager.Models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.skillstorm.taskmanager.Enums.Priority;
import com.skillstorm.taskmanager.Enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="assigned_user_id")
    private Long assignedUserId;

    @Column(name="assigned_team_id")
    private Long assignedTeamId;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name="priority")
    private Priority priority;

    @Column(name="due_date")
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    
    public Task() {
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public Long getAssignedUserId() {return assignedUserId;}
    public void setAssignedUserId(Long assignedUserId) {this.assignedUserId = assignedUserId;}

    public Long getAssignedTeamId() {return assignedTeamId;}
    public void setAssignedTeamId(Long assignedTeamId) {this.assignedTeamId = assignedTeamId;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public Status getStatus() {return status;}
    public void setStatus(Status status) {this.status = status;}

    public Priority getPriority() {return priority;}
    public void setPriority(Priority priority) {this.priority = priority;}

    public LocalDateTime getDueDate() {return dueDate;}
    public void setDueDate(LocalDateTime dueDate) {this.dueDate = dueDate;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}


}
