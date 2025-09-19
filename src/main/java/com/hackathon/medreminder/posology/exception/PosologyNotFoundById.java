package com.hackathon.medreminder.posology.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class PosologyNotFoundById extends AppException {
    public PosologyNotFoundById(Long id) {
        super(String.format("Posology with id '%s' not found", id));
    }
}
