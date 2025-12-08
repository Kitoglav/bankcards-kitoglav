package com.example.bankcards.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record DataValidationErrorResponse(String exception, String message, String uri,
                                          @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time,
                                          Map<String, List<String>> errorFields, int status) {
    public static DataValidationErrorResponse of(String message, String uri, Map<String, List<String>> errorFields) {
        return new DataValidationErrorResponse("Data Validation Error", message, uri, LocalDateTime.now(), errorFields, HttpStatus.BAD_REQUEST.value());
    }
}
