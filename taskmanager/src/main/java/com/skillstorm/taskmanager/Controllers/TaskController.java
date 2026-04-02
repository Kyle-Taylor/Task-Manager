package com.skillstorm.taskmanager.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.taskmanager.DTOs.TaskRequestDTO;
import com.skillstorm.taskmanager.Models.Task;
import com.skillstorm.taskmanager.Services.TaskService;




@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    //GET METHODS
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        try {
            return taskService.getTaskById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    //POST METHODS
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
            }
    
    //PATCH METHODS
    @PatchMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO task) {
        try {
            Task updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
          catch (Exception e) {
            return ResponseEntity.status(500).build();
    }  
}
}
