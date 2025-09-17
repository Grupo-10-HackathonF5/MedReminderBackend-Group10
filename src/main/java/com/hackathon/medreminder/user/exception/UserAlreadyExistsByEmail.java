package com.hackathon.medreminder.user.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class UserAlreadyExistsByEmail extends AppException {
    public UserAlreadyExistsByEmail(String email) {
        super(String.format("User with email '%s' already exists", email));
    }
}
