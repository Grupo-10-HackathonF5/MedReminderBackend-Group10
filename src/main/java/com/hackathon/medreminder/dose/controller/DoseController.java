package com.hackathon.medreminder.dose.controller;

import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.service.DoseService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doses")
@RequiredArgsConstructor
public class DoseController {

    private final DoseService doseService;

    @PutMapping("/{doseId}/toggle")
    public ResponseEntity<String> toggleDoseStatus(@PathVariable Long doseId) {
        String result = doseService.toggleDoseStatus(doseId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}")
    public List<DoseResponse> getDosesForUser(
            @PathVariable Long userId) {
        return doseService.getAllDosesForUser(userId);
    }

    @GetMapping("/users/{userId}/week")
    public List<DoseResponse> getDosesForCurrentWeek(
            @PathVariable Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDateTime startOfWeekDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endOfWeekDateTime = startOfWeekDateTime.plusDays(6).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return doseService.getDosesForUser(userId, startOfWeekDateTime, endOfWeekDateTime);
    }

    @GetMapping("/users/{userId}/today")
    public List<DoseResponse> getDosesForCurrentDay(
            @PathVariable Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999_999_999);
        return doseService.getDosesForUser(userId, startOfDay, endOfDay);
    }
}
