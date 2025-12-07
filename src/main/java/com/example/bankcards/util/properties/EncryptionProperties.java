package com.example.bankcards.util.properties;

import com.example.bankcards.exception.InternalValidationException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Base64;

@ConfigurationProperties("application.encryption")
public record EncryptionProperties(String secretKey) {
    public EncryptionProperties {
        validateSecretKey(secretKey);
    }

    private static void validateSecretKey(final String secretKey) throws InternalValidationException {
        if (!StringUtils.hasText(secretKey))
            throw new InternalValidationException("Encryption secret key is null or blank");
        try {
            byte[] bytes = Base64.getDecoder().decode(secretKey);
            if (bytes.length != 32) {
                throw new InternalValidationException("Encryption secret key must be 256 bits (32 bytes), got: %d bytes".formatted(bytes.length));
            }
        } catch (IllegalArgumentException e) {
            throw new InternalValidationException("Encryption secret key is not Base64");
        }
    }
}
