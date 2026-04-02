package com.skillstorm.taskmanager.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.taskmanager.Models.TaskComment;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    public List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}
