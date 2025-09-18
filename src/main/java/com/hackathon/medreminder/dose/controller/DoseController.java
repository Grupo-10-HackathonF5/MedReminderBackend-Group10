package com.hackathon.medreminder.dose.controller;

import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.service.DoseService;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/doses")
public class DoseController {

    private final DoseService doseService;

    public DoseController(DoseService doseService) {
        this.doseService = doseService;
    }

    @PatchMapping("/{doseId}/taken")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessage markDoseAsTaken(@PathVariable Long doseId) {
        doseService.markDoseAsTaken(doseId);
        return new ApiMessage("Dose marked as taken");
    }

    @GetMapping("/{userId}/today")
    public ResponseEntity<List<DoseResponse>> getDosesForUserToday(@PathVariable Long userId) {
        try {
            List<DoseResponse> doses = doseService.getDosesForUserToday(userId);
            return ResponseEntity.ok(doses);
        } catch (ParseException e) {
            // Log error appropriately
            return ResponseEntity.badRequest().build();
        }
    }
}
