package com.hackathon.medreminder.medication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "MedicationRequest", description = "Request DTO for medication creation or update")
public record MedicationRequest(

        @NotNull(message = "User ID is required")
        @Schema(description = "ID of the user owning the medication", example = "42", required = true)
        Long userId,

        @NotBlank(message = "Medication name is required")
        @Schema(description = "Name of the medication", example = "Ibuprofen", required = true)
        String name,

        @NotNull(message = "Dosage quantity is required")
        @Min(value = 1, message = "Dosage quantity must be greater than 0")
        @Schema(description = "Dosage quantity", example = "1", required = true)
        Integer dosageQuantity,

        @NotBlank(message = "Dosage unit is required")
        @Schema(description = "Dosage unit", example = "1 mg", required = true)
        String dosageUnit,

        @NotNull(message = "Active status is required")
        @Schema(description = "Whether the medication is active", example = "true", required = true)
        Boolean active,

        @Schema(description = "Optional notes about the medication", example = "Take with food")
        String notes
) {}

