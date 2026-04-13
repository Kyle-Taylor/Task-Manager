package com.skillstorm.task_manager_business_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.Exceptions.BadRequestException;
import com.skillstorm.task_manager_business_service.Exceptions.ResourceNotFoundException;
import com.skillstorm.task_manager_business_service.Models.User;
import com.skillstorm.task_manager_business_service.Repositories.TeamRepository;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public UserService(UserRepository userRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User request) {
        validateCreateRequest(request);
        validateTeamReference(request.getTeamId());

        User user = new User();
        user.setTeamId(request.getTeamId());
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getTeamId() != null) {
            validateTeamReference(request.getTeamId());
            user.setTeamId(request.getTeamId());
        }
        if (request.getUsername() != null) {
            validateUsername(request.getUsername());
            user.setUsername(request.getUsername().trim());
        }
        if (request.getEmail() != null) {
            validateEmail(request.getEmail());
            user.setEmail(request.getEmail().trim());
        }

        return userRepository.save(user);
    }

    private void validateCreateRequest(User request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        validateUsername(request.getUsername());
        validateEmail(request.getEmail());
    }

    private void validateTeamReference(Long teamId) {
        if (teamId != null && !teamRepository.existsById(teamId)) {
            throw new BadRequestException("Team not found with id: " + teamId);
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BadRequestException("username is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("email is required");
        }

        String normalized = email.trim();
        if (!normalized.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BadRequestException("email must be a valid email address");
        }
    }
}
