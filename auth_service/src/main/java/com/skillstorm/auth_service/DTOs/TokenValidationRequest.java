package com.skillstorm.auth_service.DTOs;

import jakarta.validation.constraints.NotBlank;

public record TokenValidationRequest( 
    @NotBlank(message = "Token is required")
    String token
){}
