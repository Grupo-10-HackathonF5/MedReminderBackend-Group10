package com.hackathon.medreminder.medication.dto;

import com.hackathon.medreminder.medication.entity.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    Medication toMedication(MedicationRequest medicationRequest);

    @Mapping(source = "user.id", target = "userId")
    MedicationResponse toResponse(Medication medication);
}