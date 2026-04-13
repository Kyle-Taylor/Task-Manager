package com.skillstorm.auth_service.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotNull(message = "Profile user id is required")
    Long profileUserId,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format") String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters") 
    String password,

    @NotBlank(message = "Role is required") String role
) {}
