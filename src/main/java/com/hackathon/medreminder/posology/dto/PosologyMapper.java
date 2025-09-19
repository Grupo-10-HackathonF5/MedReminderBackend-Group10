package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.posology.entity.Posology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PosologyMapper {
    Posology toPosology(PosologyRequest posologyRequest);
    @Mapping(source = "medication.name", target = "medicationName")
    @Mapping(source = "medication.id", target = "medicationId")
    PosologyResponse toResponse(Posology posology);
}
