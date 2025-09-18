package com.hackathon.medreminder.medication.controller;

import com.hackathon.medreminder.medication.dto.MedicationRequest;
import com.hackathon.medreminder.medication.dto.MedicationResponse;
import com.hackathon.medreminder.medication.service.MedicationService;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MedicationResponse> getAllMedications() {
        return medicationService.getAllMedications();
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<MedicationResponse> getMedicationById(@PathVariable Long userId) {
        return medicationService.getMedicationsByUserId(userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MedicationResponse getMedicationByUserId(@PathVariable Long id) {
        return medicationService.getMedicationById(id);
    }

    @PostMapping
    public ResponseEntity<MedicationResponse> createMedication(@Valid @RequestBody MedicationRequest medicationRequest) {
        MedicationResponse createdMedication = medicationService.createMedication(medicationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedication);
    }

    @PutMapping("/{id}")
    public MedicationResponse updateMedication(@PathVariable Long id, @Valid @RequestBody MedicationRequest medicationRequest) {
        return medicationService.updateMedication(id, medicationRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessage deleteMedication(@PathVariable Long id) {
        String message = medicationService.deleteMedication(id);
        return new ApiMessage(message);
    }
}
