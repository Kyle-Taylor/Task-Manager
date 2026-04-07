package com.skillstorm.task_manager_business_service.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skillstorm.task_manager_business_service.Models.User;
import com.skillstorm.task_manager_business_service.Repositories.UserRepository;

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
        return userRepository.save(user);
    }

    public User updateUser(Long id, User request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getTeamId() != null) { user.setTeamId(request.getTeamId()); }
        if (request.getUsername() != null) { user.setUsername(request.getUsername()); }
        if (request.getEmail() != null) { user.setEmail(request.getEmail()); }

        return userRepository.save(user);
    }
}
