package com.hackathon.medreminder.dose.controller;

import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.service.DoseService;
import com.hackathon.medreminder.shared.dto.ApiMessage;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doses")
public class DoseController {

    private final DoseService doseService;

    public DoseController(DoseService doseService) {
        this.doseService = doseService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DoseResponse>> getUserDoses(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        try {
            List<DoseResponse> doses = doseService.getDosesForUser(userId, from, to);
            return ResponseEntity.ok(doses);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{doseId}/toggle")
    public ResponseEntity<String> toggleDoseStatus(@PathVariable Long doseId) {
        String result = doseService.toggleDoseStatus(doseId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/today")
    public ResponseEntity<List<DoseResponse>> getDosesForUserToday(@PathVariable Long userId) {
        try {
            List<DoseResponse> doses = doseService.getTodayDosesForUser(userId);
            return ResponseEntity.ok(doses);
        } catch (ParseException e) {
            // Log error appropriately
            return ResponseEntity.badRequest().build();
        }
    }
}
