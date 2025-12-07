package com.example.bankcards.util.properties;

import com.example.bankcards.exception.CryptographyException;
import com.example.bankcards.exception.InternalValidationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Base64;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(String secret, long expiration) {
    public JwtProperties {
        validateJwt(secret, expiration);
    }

    private static void validateJwt(final String secret, final long expiration) throws InternalValidationException {
        if (!StringUtils.hasText(secret))
            throw new InternalValidationException("JWT secret is null or blank");
        if(expiration < 0)
            throw new InternalValidationException("JWT expiration is negative");
        try {
            byte[] bytes = Base64.getDecoder().decode(secret);
            if (bytes.length != 32) {
                throw new CryptographyException.Encryption("JWT secret must be 256 bits (32 bytes), got: %d bytes".formatted(bytes.length));
            }
        } catch (IllegalArgumentException e) {
            throw new CryptographyException.Encryption("JWT secret is not Base64");
        }
    }
}
