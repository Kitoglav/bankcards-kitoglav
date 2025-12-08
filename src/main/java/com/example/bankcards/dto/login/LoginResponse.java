package com.example.bankcards.dto.login;

import java.util.Date;

public record LoginResponse(String token, Long userId, Date generationTime, Date expirationTime) {
    public static LoginResponse of(Long userId, JwtData jwtData) {
        return new LoginResponse(jwtData.token(), userId, jwtData.issuedAt(), jwtData.expiresAt());
    }
}
