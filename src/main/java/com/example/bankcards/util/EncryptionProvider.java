package com.example.bankcards.util;

import com.example.bankcards.exception.CryptographyException;
import com.example.bankcards.util.properties.EncryptionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Component
public class EncryptionProvider {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EncryptionProperties encryptionProperties;

    public String encrypt(final String string) throws CryptographyException.Encryption {
        if (!StringUtils.hasText(string))
            throw new CryptographyException.Encryption("String for encrypt is null or blank");
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionProperties.secretKey()), KEY_ALGORITHM);
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);
            byte[] encryptedData = cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, result, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, result, GCM_IV_LENGTH, encryptedData.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (CryptographyException.Encryption e) {
            throw e;
        } catch (Exception e) {
            throw new CryptographyException.Encryption("Encryption failed: " + e.getMessage(), e);
        }
    }

    public String decrypt(final String encryptedString) throws CryptographyException.Decryption {
        if (!StringUtils.hasText(encryptedString))
            throw new CryptographyException.Decryption("String for decrypt is null or blank");
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedString);
            if (encryptedBytes.length < GCM_IV_LENGTH)
                throw new CryptographyException.Decryption("Encrypted data too short");
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[encryptedBytes.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedBytes, GCM_IV_LENGTH, cipherText, 0, cipherText.length);
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionProperties.secretKey()), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] decryptedData = cipher.doFinal(cipherText);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (CryptographyException.Decryption e) {
            throw e;
        } catch (Exception e) {
            throw new CryptographyException.Decryption("Decryption failed: " + e.getMessage(), e);
        }
    }
}
