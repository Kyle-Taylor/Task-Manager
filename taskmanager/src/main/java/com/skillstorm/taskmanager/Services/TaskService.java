package com.skillstorm.taskmanager.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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
    public Task createTask(Long assignedUserId, Long assignedTeamId, String title, String description,
                           String status, String priority, LocalDateTime dueDate) {
        Task task = new Task();
        task.setAssignedUserId(assignedUserId);
        task.setAssignedTeamId(assignedTeamId);
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);

        return taskRepository.save(task);
    }
}