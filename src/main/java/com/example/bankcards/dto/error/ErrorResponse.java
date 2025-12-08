package com.example.bankcards.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(String error, String message, String uri,
                            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time, int status) {
    public static ErrorResponse of(Exception error, String message, String uri, HttpStatus status) {
        return of(error.getClass().getSimpleName(), message, uri, status);
    }

    public static ErrorResponse of(String error, String message, String uri, HttpStatus status) {
        return new ErrorResponse(error, message, uri, LocalDateTime.now(), status.value());
    }
}
