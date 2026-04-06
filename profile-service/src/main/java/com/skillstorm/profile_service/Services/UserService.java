package com.skillstorm.profile_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.profile_service.Models.User;
import com.skillstorm.profile_service.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User request) {
        User user = new User();
        user.setTeamId(request.getTeamId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPasswordHash());
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getTeamId() != null) { user.setTeamId(request.getTeamId()); }
        if (request.getUsername() != null) { user.setUsername(request.getUsername()); }
        if (request.getEmail() != null) { user.setEmail(request.getEmail()); }
        if (request.getPasswordHash() != null) { user.setPasswordHash(request.getPasswordHash()); }
        if (request.getRole() != null) { user.setRole(request.getRole()); }
        if (request.getEnabled() != null) { user.setEnabled(request.getEnabled()); }

        return userRepository.save(user);
    }
}
