package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PosologyResponse(
        Long id,
        Long medicationId,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime dayTime,
        Integer frequencyValue,
        FrequencyUnit frequencyUnit,
        Double quantity,
        String reminderMessage,
        Double dosesNumber
) {}
