package com.example.bankcards.exception;


import com.example.bankcards.dto.error.DataValidationErrorResponse;
import com.example.bankcards.dto.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static String getUri(final WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataValidationErrorResponse> handleDataValidationExceptions(final MethodArgumentNotValidException exception, final WebRequest request) {
        Map<String, List<String>> errorFields = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errorFields.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });
        DataValidationErrorResponse response = DataValidationErrorResponse.of(exception.getMessage(), getUri(request), errorFields);
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<DataValidationErrorResponse> handleMethodValidationExceptions(
            final HandlerMethodValidationException exception,
            final WebRequest request) {
        Map<String, List<String>> errorFields = new HashMap<>();
        exception.getBeanResults().forEach(beanResult -> {
            beanResult.getFieldErrors().forEach(error -> {
                String fieldName = error.getField();
                String errorMessage = error.getDefaultMessage();
                errorFields.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            });
        });
        exception.getValueResults().forEach(valueResult -> {
            valueResult.getResolvableErrors().forEach(error -> {
                String fieldName = valueResult.getMethodParameter().getParameterName();
                String errorMessage = error.getDefaultMessage();
                errorFields.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            });
        });

        DataValidationErrorResponse response = DataValidationErrorResponse.of(
                exception.getMessage(), getUri(request), errorFields);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedExceptions(final AccessDeniedException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(final AuthenticationException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CryptographyException.class)
    public ResponseEntity<ErrorResponse> handleCryptographyExceptions(final CryptographyException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsExceptions(final EntityAlreadyExistsException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundExceptions(final EntityNotFoundException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalExceptions(final InternalException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ErrorResponse> handleTransferExceptions(final TransferException exception, final WebRequest request) {
        return makeErrorResponse(exception, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleVanillaExceptions(final Exception exception, final WebRequest request) {
        return makeErrorResponse("INTERNAL SERVER ERROR", "{%s}: %s".formatted(exception.getClass().getSimpleName(), exception.getMessage()), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private <T extends Exception> ResponseEntity<ErrorResponse> makeErrorResponse(final T e, final WebRequest request, HttpStatus status) {
        ErrorResponse response = ErrorResponse.of(e, e.getMessage(), getUri(request), status);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponse(String error, String message, final WebRequest request, HttpStatus status) {
        ErrorResponse response = ErrorResponse.of(error, message, getUri(request), status);
        return ResponseEntity.status(status).body(response);
    }
}
