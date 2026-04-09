package com.skillstorm.auth_service.DTOs;

public record LoginResponse (
    String token,
    Long profileUserId,
    String email,
    String role,
    Long expiresIn
) {}
    

