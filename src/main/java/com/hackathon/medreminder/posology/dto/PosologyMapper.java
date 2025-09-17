package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.entity.Posology;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PosologyMapper {
    Posology toPosology(PosologyRequest posologyRequest);
    PosologyResponse toResponse(Posology posology);
}
