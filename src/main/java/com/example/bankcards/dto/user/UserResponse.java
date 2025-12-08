package com.example.bankcards.dto.user;

import com.example.bankcards.entity.impl.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserResponse(Long id, String username, String role, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime updatedAt) {
    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().toString(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
