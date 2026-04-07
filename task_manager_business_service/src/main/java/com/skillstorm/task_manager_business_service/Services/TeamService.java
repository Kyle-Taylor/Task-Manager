package com.skillstorm.task_manager_business_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.Models.Team;
import com.skillstorm.task_manager_business_service.Repositories.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public Team createTeam(Team request) {
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setTeamLeadId(request.getTeamLeadId());
        return teamRepository.save(team);
    }

    public Team updateTeam(Long id, Team request) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        if (request.getName() != null) { team.setName(request.getName()); }
        if (request.getDescription() != null) { team.setDescription(request.getDescription()); }
        if (request.getTeamLeadId() != null) { team.setTeamLeadId(request.getTeamLeadId()); }

        return teamRepository.save(team);
    }
}
