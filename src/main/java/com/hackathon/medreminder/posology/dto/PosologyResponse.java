package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PosologyResponse(
        Long id,
        Long medicationId,
        String medicationName,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime dayTime,
        Integer frequencyValue,
        FrequencyUnit frequencyUnit,
        Double quantity,
        String reminderMessage,
        Double dosesNumber
) {}
