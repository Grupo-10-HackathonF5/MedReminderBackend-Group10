package com.hackathon.medreminder.dose.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DoseResponse(
        Long id,
        Long reminderId,
        LocalDate scheduledDay,
        LocalDateTime scheduledDateTime,
        Boolean taken,
        LocalDateTime takenTime,
        Long posologyId
) {}
