package com.example.bankcards.util;


import com.example.bankcards.exception.AuthenticationException;
import com.example.bankcards.util.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public final class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(final UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims validateToken(final String token) throws AuthenticationException.InvalidToken {
        try {
            return generateClaims(token);
        } catch (SignatureException e) {
            throw new AuthenticationException.InvalidToken("JWT token signature is invalid: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new AuthenticationException.InvalidToken("JWT token is malformed: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException.InvalidToken("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException.InvalidToken("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException.InvalidToken("JWT claims string is illegal: " + e.getMessage());
        }
    }

    private Claims generateClaims(final String token) {
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(final String token) {
        try {
            return this.validateToken(token).getSubject();
        } catch (AuthenticationException.InvalidToken e) {
            log.error("Error reading username from token: {}", e.getMessage());
            return null;
        }

    }
}
