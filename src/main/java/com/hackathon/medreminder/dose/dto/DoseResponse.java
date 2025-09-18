package com.hackathon.medreminder.dose.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record DoseResponse(
        Long doseId,
        Long posologyId,
        String medicationName,
        Double dosesNumber,
        Long medicationId,
        LocalDate scheduledDay,
        LocalDateTime scheduledDateTime,
        Boolean isTaken,
        LocalDateTime takenTime
) {}