package com.skillstorm.taskmanager.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.taskmanager.Models.TaskComment;
import com.skillstorm.taskmanager.Repositories.TaskCommentRepository;

@Service
public class TaskCommentService {


    // GET METHODS
    public List<TaskComment> getCommentsByTaskId(Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    // DELETE METHODS
    public void deleteComment(Long commentId) {
        taskCommentRepository.deleteById(commentId);
    }

    // PATCH METHODS
    public TaskComment updateComment(Long commentId, String newText) {
        TaskComment comment = taskCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.setCommentText(newText);
        return taskCommentRepository.save(comment);
    }
    private final TaskCommentRepository taskCommentRepository;
    public TaskCommentService(TaskCommentRepository taskCommentRepository) {
        this.taskCommentRepository = taskCommentRepository;
    }

}
