package com.hackathon.medreminder.medication.dto;

import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    @Mapping(source = "user", target = "user")
    Medication toMedication(MedicationRequest medicationRequest, User user);

    @Mapping(source = "user.id", target = "userId")
    MedicationResponse toResponse(Medication medication);
}