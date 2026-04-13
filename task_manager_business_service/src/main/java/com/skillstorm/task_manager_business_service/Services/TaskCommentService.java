package com.skillstorm.task_manager_business_service.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.DTOs.TaskCommentRequestDTO;
import com.skillstorm.task_manager_business_service.Exceptions.BadRequestException;
import com.skillstorm.task_manager_business_service.Exceptions.InvalidReferenceException;
import com.skillstorm.task_manager_business_service.Exceptions.ResourceNotFoundException;
import com.skillstorm.task_manager_business_service.Models.TaskComment;
import com.skillstorm.task_manager_business_service.Repositories.TaskRepository;
import com.skillstorm.task_manager_business_service.Repositories.TaskCommentRepository;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;

@Service
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskService taskService;

    public TaskCommentService(
        TaskCommentRepository taskCommentRepository,
        TaskRepository taskRepository,
        UserRepository userRepository,
        TaskService taskService
    ) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    public List<TaskComment> getCommentsByTaskId(Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    public TaskComment createComment(TaskCommentRequestDTO request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.getTaskId() == null) {
            throw new BadRequestException("taskId is required");
        }

        if (request.getUserId() == null) {
            throw new BadRequestException("userId is required");
        }

        if (request.getCommentText() == null || request.getCommentText().trim().isEmpty()) {
            throw new BadRequestException("commentText is required");
        }

        if (!taskRepository.existsById(request.getTaskId())) {
            throw new InvalidReferenceException("Task not found with id: " + request.getTaskId());
        }

        if (!userRepository.existsById(request.getUserId())) {
            throw new InvalidReferenceException("User not found with id: " + request.getUserId());
        }

        TaskComment comment = new TaskComment();
        comment.setTaskId(request.getTaskId());
        comment.setUserId(request.getUserId());
        comment.setCommentText(request.getCommentText().trim());

        TaskComment savedComment = taskCommentRepository.save(comment);
        taskService.markTaskAsUnread(request.getTaskId());
        return savedComment;
    }

    public void deleteComment(Long commentId) {
        if (!taskCommentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment not found with id: " + commentId);
        }
        taskCommentRepository.deleteById(commentId);
    }

    public TaskComment updateComment(Long commentId, String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new BadRequestException("comment text is required");
        }

        TaskComment comment = taskCommentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        comment.setCommentText(newText.trim());
        return taskCommentRepository.save(comment);
    }
}
