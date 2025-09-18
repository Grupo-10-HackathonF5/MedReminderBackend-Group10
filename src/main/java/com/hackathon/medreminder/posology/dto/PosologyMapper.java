package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.entity.Posology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PosologyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medication", ignore = true)
    @Mapping(target = "user", ignore = true)
    Posology toPosology(PosologyRequest posologyRequest);

    @Mapping(source = "medication.name", target = "medicationName")
    @Mapping(source = "medication.id", target = "medicationId")
    PosologyResponse toResponse(Posology posology);
}