package com.skillstorm.profile_service.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillstorm.profile_service.Models.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
