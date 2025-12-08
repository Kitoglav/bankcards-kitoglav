package com.example.bankcards.dto.login;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record LoginResponse(String token, Long userId, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date generationTime, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date expirationTime) {
    public static LoginResponse of(Long userId, JwtData jwtData) {
        return new LoginResponse(jwtData.token(), userId, jwtData.issuedAt(), jwtData.expiresAt());
    }
}
