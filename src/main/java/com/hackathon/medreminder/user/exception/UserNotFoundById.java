package com.hackathon.medreminder.user.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class UserNotFoundById extends AppException {
    public UserNotFoundById(Long id) {
        super(String.format("User with id '%s' not found", id));
    }
}
