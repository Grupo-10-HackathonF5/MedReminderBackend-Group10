package com.hackathon.medreminder.medication.dto;

import com.hackathon.medreminder.medication.entity.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "posologies", ignore = true)
    Medication toMedication(MedicationRequest medicationRequest);

    @Mapping(source = "user.id", target = "userId")
    MedicationResponse toResponse(Medication medication);
}