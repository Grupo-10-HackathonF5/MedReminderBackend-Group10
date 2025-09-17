package com.hackathon.medreminder.user.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class UserNotFoundByUsername extends AppException {
    public UserNotFoundByUsername(String username) {
        super(String.format("User with username '%s' not found", username));
    }
}
