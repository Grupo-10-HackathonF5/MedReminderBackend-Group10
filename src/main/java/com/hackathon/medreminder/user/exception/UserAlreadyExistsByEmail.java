package com.hackathon.medreminder.user.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class UserAlreadyExistsByUsername extends AppException {
    public UserAlreadyExistsByUsername(String username) {
        super(String.format("Username '%s' not found", username));
    }
}
