package com.hackathon.medreminder.medication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MedicationResponse", description = "Response DTO for medication")
public record MedicationResponse(

        @Schema(description = "Medication identifier", example = "123")
        Long id,

        @Schema(description = "User identifier who owns the medication", example = "42")
        Long userId,

        @Schema(description = "Name of the medication", example = "Ibuprofen")
        String name,

        @Schema(description = "Dosage quantity", example = "1")
        Integer dosageQuantity,

        @Schema(description = "Dosage unit", example = "1 mg")
        String dosageUnit,

        @Schema(description = "Whether the medication is active", example = "true")
        Boolean active,

        @Schema(description = "Optional notes about the medication", example = "Take with food")
        String notes
) {}
