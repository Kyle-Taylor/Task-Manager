package com.skillstorm.task_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_service.Clients.ProfileServiceClient;
import com.skillstorm.task_service.DTOs.TaskRequestDTO;
import com.skillstorm.task_service.Models.Task;
import com.skillstorm.task_service.Repositories.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProfileServiceClient profileServiceClient;

    public TaskService(TaskRepository taskRepository, ProfileServiceClient profileServiceClient) {
        this.taskRepository = taskRepository;
        this.profileServiceClient = profileServiceClient;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task createTask(TaskRequestDTO request) {
        validateTaskReferences(request);

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

    public Task updateTask(Long id, TaskRequestDTO request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        validateTaskReferences(request);

        if (request.getAssignedUserId() != null) { task.setAssignedUserId(request.getAssignedUserId()); }
        if (request.getAssignedTeamId() != null) { task.setAssignedTeamId(request.getAssignedTeamId()); }
        if (request.getTitle() != null) { task.setTitle(request.getTitle()); }
        if (request.getDescription() != null) { task.setDescription(request.getDescription()); }
        if (request.getStatus() != null) { task.setStatus(request.getStatus()); }
        if (request.getPriority() != null) { task.setPriority(request.getPriority()); }
        if (request.getDueDate() != null) { task.setDueDate(request.getDueDate()); }

        return taskRepository.save(task);
    }

    private void validateTaskReferences(TaskRequestDTO request) {
        profileServiceClient.validateUserExists(request.getAssignedUserId());
        profileServiceClient.validateTeamExists(request.getAssignedTeamId());
    }
}
