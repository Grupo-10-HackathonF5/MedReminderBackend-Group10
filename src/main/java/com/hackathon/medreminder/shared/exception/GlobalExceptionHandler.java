package com.hackathon.medreminder.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage() != null ? exception.getMessage() : "Unexpected error");

        ErrorResponse body = new ErrorResponse(status, errors, request);
        return ResponseEntity.status(status).body(body);
    }
}
