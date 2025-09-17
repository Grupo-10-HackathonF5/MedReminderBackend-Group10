package com.hackathon.medreminder.medication.exception;

import com.hackathon.medreminder.shared.exception.AppException;

public class MedicationNotFoundById extends AppException {
    public MedicationNotFoundById(Long id) {
        super(String.format("Medication with id '%s' not found", id));
    }
}
