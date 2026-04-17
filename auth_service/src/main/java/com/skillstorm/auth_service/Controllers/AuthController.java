package com.skillstorm.auth_service.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.skillstorm.auth_service.DTOs.LoginRequest;
import com.skillstorm.auth_service.DTOs.LoginResponse;
import com.skillstorm.auth_service.DTOs.RegisterRequest;
import com.skillstorm.auth_service.DTOs.TokenValidationResponse;
import com.skillstorm.auth_service.Services.AuthService;
import com.skillstorm.auth_service.DTOs.TokenValidationRequest;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
        Route: POST /auth/register
        Description: Registers a new user with the provided email, password, and role.
        Request Body: JSON object containing "email", "password", and "role" fields.
        Response: 200 OK with a success message if registration is successful, 
        or an error message if registration fails.
    */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    /*
        Route: POST /auth/login
        Description: Authenticates a user with the provided email and password.
        Request Body: JSON object containing "email" and "password" fields.
        Response: 200 OK with a JWT token if authentication is successful,
        or an error message if authentication fails.
    */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /*
        Route: POST /auth/validate
        Description: Validates a JWT token.
        Request Body: JSON object containing "token" field.
        Response: 200 OK with token validation result.
    */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validate(
        @Valid
        @RequestBody 
        TokenValidationRequest request) {
        TokenValidationResponse response = authService.validateToken(request.token());
        return ResponseEntity.ok(response);
    }

    /*
        Route: GET /auth/health
        Description: Checks the health of the Auth Service.
        Response: 200 OK with a message indicating the service is healthy.
    */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is healthy");
    }
}
