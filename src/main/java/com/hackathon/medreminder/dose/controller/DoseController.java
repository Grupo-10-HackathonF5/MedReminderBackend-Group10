package com.hackathon.medreminder.dose.controller;

import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.service.DoseService;
import com.hackathon.medreminder.shared.dto.ApiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doses")
@RequiredArgsConstructor
public class DoseController {

    private final DoseService doseService;

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<DoseResponse> getUserDoses(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return doseService.getDosesForUser(userId, from, to);
    }

    @PatchMapping("/{doseId}/taken")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessage markDoseAsTaken(@PathVariable Long doseId) {
        doseService.markDoseAsTaken(doseId);
        return new ApiMessage("Dose marked as taken");
    }

    @GetMapping("/user/{userId}/today")
    @ResponseStatus(HttpStatus.OK)
    public List<DoseResponse> getTodayDoses(@PathVariable Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
        return doseService.getDosesForUser(userId, startOfDay, endOfDay);
    }

}