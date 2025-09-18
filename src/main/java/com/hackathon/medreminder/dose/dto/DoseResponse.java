package com.hackathon.medreminder.dose.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DoseResponse(
        Long id,
        Long posologyId,
        String medicationName,
        Double dosesNumber,
        Long medicationId,
        LocalDate scheduledDay,
        LocalDateTime scheduledDateTime,
        Boolean isTaken,
        LocalDateTime takenTime,
        Boolean takeDuringMeal
) {}