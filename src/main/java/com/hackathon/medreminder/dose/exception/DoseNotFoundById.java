package com.hackathon.medreminder.dose.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class DoseNotFoundById extends AppException {
    public DoseNotFoundById(Long id) {
        super(String.format("Dose with id '%s' not found", id));
    }
}
