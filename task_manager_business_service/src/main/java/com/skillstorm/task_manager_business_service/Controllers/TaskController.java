package com.skillstorm.task_manager_business_service.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.task_manager_business_service.DTOs.TaskRequestDTO;
import com.skillstorm.task_manager_business_service.Exceptions.ResourceNotFoundException;
import com.skillstorm.task_manager_business_service.Models.Task;
import com.skillstorm.task_manager_business_service.Services.TaskService;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
        @RequestParam(defaultValue = "updated") String sortBy,
        @RequestParam(defaultValue = "desc") String direction
    ) {
        return ResponseEntity.ok(taskService.getAllTasks(sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody JsonNode task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Task> markTaskAsRead(@PathVariable Long id, @RequestParam Long viewerUserId) {
        return ResponseEntity.ok(taskService.markTaskAsRead(id, viewerUserId));
    }
}
