package com.example.blog.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map; // Import Map

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        Map<String, String> errorDetails = Map.of("message", "Invalid email or password");
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // This handler can also be updated for consistency
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String message = "An internal server error occurred";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex.getMessage().contains("Email already exists") || ex.getMessage().contains("Username is already taken")) {
            message = ex.getMessage();
            status = HttpStatus.CONFLICT;
        }

        Map<String, String> errorDetails = Map.of("message", message);
        return new ResponseEntity<>(errorDetails, status);
    }
}