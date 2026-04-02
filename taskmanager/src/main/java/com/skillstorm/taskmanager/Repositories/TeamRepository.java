package com.skillstorm.taskmanager.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.taskmanager.Models.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
}
