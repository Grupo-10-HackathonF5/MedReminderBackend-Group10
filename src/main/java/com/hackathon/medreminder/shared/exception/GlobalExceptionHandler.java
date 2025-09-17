package com.hackathon.medreminder.shared.exception;

import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.user.exception.UserAlreadyExistsByEmail;
import com.hackathon.medreminder.user.exception.UserAlreadyExistsByUsername;
import com.hackathon.medreminder.user.exception.UserNotFoundByUsername;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundByUsername.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundByUsername(UserNotFoundByUsername exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(PosologyNotFoundById.class)
    public ResponseEntity<ErrorResponse> handlePosologyNotFoundById(PosologyNotFoundById exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsByUsername.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsByUsername(UserAlreadyExistsByUsername exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse body = new ErrorResponse(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(UserAlreadyExistsByEmail.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsByEmail(UserAlreadyExistsByEmail exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse body = new ErrorResponse(status, exception.getMessage(), request);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage() != null ? exception.getMessage() : "Unexpected error");

        ErrorResponse body = new ErrorResponse(status, errors, request);
        return ResponseEntity.status(status).body(body);
    }
}
