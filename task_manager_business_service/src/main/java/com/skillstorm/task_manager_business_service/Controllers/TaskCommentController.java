package com.skillstorm.task_manager_business_service.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillstorm.task_manager_business_service.Models.TaskComment;
import com.skillstorm.task_manager_business_service.Services.TaskCommentService;

@RestController
@RequestMapping("/api/comments")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    public TaskCommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskComment>> getCommentsByTaskId(@PathVariable Long taskId) {
        try {
            return ResponseEntity.ok(taskCommentService.getCommentsByTaskId(taskId));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            taskCommentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<TaskComment> updateComment(@PathVariable Long commentId, @RequestBody String newText) {
        try {
            return ResponseEntity.ok(taskCommentService.updateComment(commentId, newText));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
