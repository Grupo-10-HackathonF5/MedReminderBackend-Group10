package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PosologyRequest(

        @NotNull(message = "Medication ID is required")
        Long medicationId,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate,

        @NotNull(message = "Day time is required")
        LocalDateTime dayTime,

        @NotNull(message = "Frequency value is required")
        @Min(value = 1, message = "Frequency value must be greater than 0")
        Integer frequencyValue, // Ejemplo: 8 (para "cada 8 horas")

        @NotNull(message = "Frequency unit is required")
        FrequencyUnit frequencyUnit, // Ejemplo: HOUR (para "cada 8 horas")

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
        Double quantity,

        String reminderMessage,

        @NotNull(message = "Doses number is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Doses number must be greater than 0")
        Double dosesNumber
) {}
