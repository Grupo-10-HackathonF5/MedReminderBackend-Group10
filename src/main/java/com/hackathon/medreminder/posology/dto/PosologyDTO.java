package com.hackathon.medreminder.posology.dto;

import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosologyDTO {
    
    private Long id;
    
    @NotNull(message = "Medication ID is required")
    private Long medicationId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Day time is required")
    private LocalDateTime dayTime;
    
    @NotNull(message = "Frequency value is required")
    @Min(value = 1, message = "Frequency value must be greater than 0")
    private Integer frequencyValue; // Ejemplo: 8 (para "cada 8 horas")
    
    @NotNull(message = "Frequency unit is required")
    private FrequencyUnit frequencyUnit; // Ejemplo: HOUR (para "cada 8 horas")
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than 0")
    private Double quantity;
    
    private String reminderMessage;
    
    @NotNull(message = "Doses number is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Doses number must be greater than 0")
    private Double dosesNumber;
}