package com.skillstorm.task_manager_business_service.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.Models.TaskComment;
import com.skillstorm.task_manager_business_service.Repositories.TaskCommentRepository;

@Service
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;

    public TaskCommentService(TaskCommentRepository taskCommentRepository) {
        this.taskCommentRepository = taskCommentRepository;
    }

    public List<TaskComment> getCommentsByTaskId(Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    public void deleteComment(Long commentId) {
        taskCommentRepository.deleteById(commentId);
    }

    public TaskComment updateComment(Long commentId, String newText) {
        TaskComment comment = taskCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        comment.setCommentText(newText);
        return taskCommentRepository.save(comment);
    }
}
