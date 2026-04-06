package com.skillstorm.task_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.task_service.Models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
