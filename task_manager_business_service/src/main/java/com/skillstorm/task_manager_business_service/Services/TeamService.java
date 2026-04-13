package com.skillstorm.task_manager_business_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.Exceptions.BadRequestException;
import com.skillstorm.task_manager_business_service.Exceptions.ResourceNotFoundException;
import com.skillstorm.task_manager_business_service.Models.Team;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;
import com.skillstorm.task_manager_business_service.Repositories.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public Team createTeam(Team request) {
        validateCreateRequest(request);
        validateTeamLeadReference(request.getTeamLeadId());

        Team team = new Team();
        team.setName(request.getName().trim());
        team.setDescription(request.getDescription());
        team.setTeamLeadId(request.getTeamLeadId());
        return teamRepository.save(team);
    }

    public Team updateTeam(Long id, Team request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));

        if (request.getName() != null) {
            validateName(request.getName());
            team.setName(request.getName().trim());
        }
        if (request.getDescription() != null) { team.setDescription(request.getDescription()); }
        if (request.getTeamLeadId() != null) {
            validateTeamLeadReference(request.getTeamLeadId());
            team.setTeamLeadId(request.getTeamLeadId());
        }

        return teamRepository.save(team);
    }

    private void validateCreateRequest(Team request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        validateName(request.getName());
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("name is required");
        }
    }

    private void validateTeamLeadReference(Long teamLeadId) {
        if (teamLeadId != null && !userRepository.existsById(teamLeadId)) {
            throw new BadRequestException("User not found with id: " + teamLeadId);
        }
    }
}
