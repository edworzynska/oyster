package com.example.oyster.configuration;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        HttpStatus status = determineHttpStatus(ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("status", status.value());

        return new ResponseEntity<>(errorResponse, status);
    }
    
    private HttpStatus determineHttpStatus(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof UserNotAuthenticatedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof EntityExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof EntityNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof IllegalStateException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
