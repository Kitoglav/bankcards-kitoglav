package com.example.bankcards.dto.login;


import java.util.Date;

public record JwtData(String token, Date issuedAt, Date expiresAt) {
}
