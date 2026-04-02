package com.skillstorm.taskmanager.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.skillstorm.taskmanager.Services.TaskCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import com.skillstorm.taskmanager.Models.TaskComment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/comments")
public class TaskCommentController {
    private final TaskCommentService taskCommentService;
    public TaskCommentController(TaskCommentService taskCommentService) {
        this.taskCommentService = taskCommentService;
    }

    //GET METHODS
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskComment>> getCommentsByTaskId(@PathVariable Long taskId
    ) {
        try {
            List<TaskComment> comments = taskCommentService.getCommentsByTaskId(taskId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    //DELETE METHODS
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            taskCommentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    //PATCH METHODS
    @PatchMapping("/{commentId}")
    public ResponseEntity<TaskComment> updateComment(@PathVariable Long commentId, @RequestBody String newText) {
        try {
            TaskComment updatedComment = taskCommentService.updateComment(commentId, newText);
            return ResponseEntity.ok(updatedComment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
}}