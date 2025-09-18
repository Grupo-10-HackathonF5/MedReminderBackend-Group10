package com.hackathon.medreminder.dose.dto;

import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.posology.entity.Posology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface DoseMapper {

    @Mapping(source = "dose.posology.id", target = "posologyId")
    @Mapping(source = "scheduledDateTime", target = "scheduledDateTime")
    @Mapping(source = "dose.scheduledDay", target = "scheduledDay")
    DoseResponse toResponse(Dose dose, LocalDateTime scheduledDateTime);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posologyId", source = "posology.id")
    @Mapping(target = "scheduledDateTime", source = "scheduledDateTime")
    @Mapping(target = "scheduledDay", expression = "java(scheduledDateTime.toLocalDate())")
    @Mapping(target = "taken", constant = "false")
    @Mapping(target = "takenTime", ignore = true)
    @Mapping(target = "reminderId", ignore = true)
    DoseResponse fromRecurrence(Posology posology, LocalDateTime scheduledDateTime);
}