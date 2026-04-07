package com.skillstorm.task_manager_business_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.DTOs.TaskRequestDTO;
import com.skillstorm.task_manager_business_service.Exceptions.InvalidReferenceException;
import com.skillstorm.task_manager_business_service.Models.Task;
import com.skillstorm.task_manager_business_service.Repositories.TaskRepository;
import com.skillstorm.task_manager_business_service.Repositories.TeamRepository;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public TaskService(
        TaskRepository taskRepository,
        UserRepository userRepository,
        TeamRepository teamRepository
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
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
        if (request.getAssignedUserId() != null && !userRepository.existsById(request.getAssignedUserId())) {
            throw new InvalidReferenceException("User not found with id: " + request.getAssignedUserId());
        }

        if (request.getAssignedTeamId() != null && !teamRepository.existsById(request.getAssignedTeamId())) {
            throw new InvalidReferenceException("Team not found with id: " + request.getAssignedTeamId());
        }
    }
}
