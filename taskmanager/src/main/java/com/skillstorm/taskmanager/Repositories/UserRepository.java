package com.skillstorm.taskmanager.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.taskmanager.Models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
