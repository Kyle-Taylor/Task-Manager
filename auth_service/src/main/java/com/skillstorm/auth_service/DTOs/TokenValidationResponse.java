package com.skillstorm.auth_service.DTOs;

public record TokenValidationResponse(
    boolean valid,
    Long profileUserId,
    String email,
    String role
) {}
