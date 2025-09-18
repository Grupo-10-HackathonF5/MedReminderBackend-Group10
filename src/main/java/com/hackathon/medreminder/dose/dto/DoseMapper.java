package com.hackathon.medreminder.dose.dto;

import com.hackathon.medreminder.dose.entity.Dose;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoseMapper {

    @Mapping(source = "dose.id", target = "doseId")
    @Mapping(source = "dose.posology.id", target = "posologyId")
    @Mapping(source = "dose.posology.medication.name", target = "medicationName")
    @Mapping(source = "dose.posology.dosesNumber", target = "dosesNumber")
    @Mapping(source = "dose.posology.medication.id", target = "medicationId")
    DoseResponse toResponse(Dose dose);

}
