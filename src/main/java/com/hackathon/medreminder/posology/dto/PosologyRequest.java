package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "PosologyRequest", description = "Request DTO for posology creation or update")
public record PosologyRequest(

        @NotNull(message = "Medication ID is required")
        @Schema(description = "ID of the medication", example = "10", required = true)
        Long medicationId,

        @NotNull(message = "User ID is required")
        @Schema(description = "ID of the user", example = "1", required = true)
        Long userId,

        @NotNull(message = "Start date is required")
        @Schema(description = "Start date for the posology", example = "2025-09-18", required = true)
        LocalDate startDate,

        @NotNull(message = "End date is required")
        @Schema(description = "End date for the posology", example = "2025-12-31", required = true)
        LocalDate endDate,

        @NotNull(message = "Day time is required")
        @Schema(description = "Time of day for taking medication", example = "2025-09-18T08:00:00", required = true)
        LocalDateTime dayTime,

        @NotNull(message = "Frequency value is required")
        @Min(value = 1, message = "Frequency value must be greater than 0")
        @Schema(description = "Frequency number for medication intake", example = "8", required = true)
        Integer frequencyValue, // Ejemplo: 8 (para "cada 8 horas")

        @NotNull(message = "Frequency unit is required")
        @Schema(description = "Unit for frequency (e.g., HOUR, DAY)", example = "HOUR", required = true)
        FrequencyUnit frequencyUnit, // Ejemplo: HOUR (para "cada 8 horas")

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
        @Schema(description = "Quantity of medication per dose", example = "1.0", required = true)
        Double quantity,

        @Schema(description = "Optional reminder message for medication", example = "Take after meal")
        String reminderMessage,

        @NotNull(message = "Doses number is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Doses number must be greater than 0")
        @Schema(description = "Total number of doses", example = "10", required = true)
        Double dosesNumber
) {}
