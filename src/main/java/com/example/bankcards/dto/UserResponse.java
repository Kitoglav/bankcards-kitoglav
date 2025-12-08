package com.example.bankcards.dto;

import com.example.bankcards.entity.impl.User;

import java.time.LocalDateTime;

public record UserResponse(Long id, String username, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().toString(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
