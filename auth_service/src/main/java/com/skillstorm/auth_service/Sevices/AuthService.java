package com.skillstorm.auth_service.Sevices;

import org.springframework.stereotype.Service;

import com.skillstorm.auth_service.DTOs.LoginResponse;
import com.skillstorm.auth_service.DTOs.RegisterRequest;
import com.skillstorm.auth_service.Models.UserCredentials;
import com.skillstorm.auth_service.Repositories.UserCredentialsRepository;
import com.skillstorm.auth_service.DTOs.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.skillstorm.auth_service.DTOs.TokenValidationResponse;

@Service
public class AuthService {
    private final UserCredentialsRepository userCredentialsRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserCredentialsRepository userCredentialsRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /*
        Description: Registers a new user with the provided email, password, and role.
        Status Codes: 
            200 OK - User registered successfully
            400 Bad Request - Invalid input data
            500 Internal Server Error - Server error during registration
    */
    public void register(RegisterRequest request){
        UserCredentials user = new UserCredentials(
            request.profileUserId(),
            request.email(),
            passwordEncoder.encode(request.password()),
            request.role()
        );
        userCredentialsRepository.save(user);
    }

    /*
        Description: Authenticates a user with the provided email and password.
        Status Codes: 
            200 OK - User authenticated successfully
            400 Bad Request - Invalid input data
            401 Unauthorized - Invalid email or password
            500 Internal Server Error - Server error during authentication
    */
    public LoginResponse login(LoginRequest request){
        UserCredentials user = userCredentialsRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole(),
            user.getProfileUserId()
        );

        return new LoginResponse(
            token, 
            user.getProfileUserId(), 
            user.getEmail(), 
            user.getRole(),
            jwtService.extractExpiration(token)
        );
    }

    /*
        Description: Validates the provided JWT token.
        Status Codes: 
            200 OK - Token is valid
            401 Unauthorized - Token is invalid
    */
    public TokenValidationResponse validateToken(String token){
        boolean valid = jwtService.validateToken(token);

        if(!valid){
            return new TokenValidationResponse(false, null, null, null);
        }

        return new TokenValidationResponse(
            true,
            jwtService.extractUserId(token),
            jwtService.extractEmail(token),
            jwtService.extractRole(token)
        );
    }
}

