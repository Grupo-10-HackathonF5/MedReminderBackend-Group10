package com.hackathon.medreminder.medication.dto;

import com.hackathon.medreminder.medication.entity.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    Medication toMedication(MedicationRequest medicationRequest);
    MedicationResponse toResponse(Medication medication);
}
