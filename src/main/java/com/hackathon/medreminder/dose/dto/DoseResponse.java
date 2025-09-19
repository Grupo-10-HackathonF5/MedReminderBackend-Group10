package com.hackathon.medreminder.dose.dto;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit; // Importar FrequencyUnit

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
        LocalDateTime takenTime,
        String notes,
                LocalDate startDate,
        LocalDate endDate,
        Integer frequencyValue,
        FrequencyUnit frequencyUnit
) {}