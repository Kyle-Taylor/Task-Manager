package com.skillstorm.taskmanager.Services;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.taskmanager.DTOs.TaskRequestDTO;
import com.skillstorm.taskmanager.Models.Task;
import com.skillstorm.taskmanager.Repositories.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // GET METHODS
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // POST METHODS
    public Task createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setAssignedUserId(request.getAssignedUserId());
        task.setAssignedTeamId(request.getAssignedTeamId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        return taskRepository.save(task);
    }

    // PATCH METHODS
    public Task updateTask(Long id, TaskRequestDTO request) {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

    if (request.getAssignedUserId() != null) {task.setAssignedUserId(request.getAssignedUserId());}
    if (request.getAssignedTeamId() != null) {task.setAssignedTeamId(request.getAssignedTeamId());}
    if (request.getTitle() != null) {task.setTitle(request.getTitle());}
    if (request.getDescription() != null) {task.setDescription(request.getDescription());}
    if (request.getStatus() != null) {task.setStatus(request.getStatus());}
    if (request.getPriority() != null) {task.setPriority(request.getPriority());}
    if (request.getDueDate() != null) {task.setDueDate(request.getDueDate());}

    return taskRepository.save(task);
}
}