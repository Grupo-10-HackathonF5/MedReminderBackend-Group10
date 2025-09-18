package com.hackathon.medreminder.dose.dto;

import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.posology.entity.Posology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface DoseMapper {
    DoseResponse toResponse(Dose dose, LocalDateTime scheduledDateTime);

    @Mapping(target = "posologyId", source = "posology.id")
    @Mapping(target = "scheduledDateTime", source = "scheduledDateTime")
    @Mapping(target = "taken", constant = "false")
    @Mapping(target = "takenTime", ignore = true)
    DoseResponse fromRecurrence(Posology posology, LocalDateTime scheduledDateTime);

    @Mapping(source = "posology.id", target = "posologyId")
    @Mapping(source = "posology.medication.name", target = "medicationName")
    @Mapping(source = "posology.dosesNumber", target = "dosesNumber")
    @Mapping(source = "posology.medication.id", target = "medicationId")
    @Mapping(source = "posology.takeDuringMeal", target = "takeDuringMeal")
    DoseResponse toResponse(Dose dose);
}
