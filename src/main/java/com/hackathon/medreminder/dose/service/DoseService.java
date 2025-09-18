package com.hackathon.medreminder.dose.service;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.exception.DoseNotFoundById;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Recur;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoseService {
    private final DoseRepository doseRepository;
    private final PosologyRepository posologyRepository;
    private final DoseMapper doseMapper;

    public List<DoseResponse> generateOccurrences(Posology posology, LocalDateTime from, LocalDateTime to) throws ParseException {
        // Build RRULE string from Posology frequency fields
        String rrule = "FREQ=" + posology.getFrequencyUnit().name() +
                ";INTERVAL=" + posology.getFrequencyValue();
        if (posology.getEndDate() != null) {
            rrule += ";UNTIL=" + posology.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T235959Z";
        }

        Recur recur = new Recur(rrule);

        ZonedDateTime start = posology.getDayTime().atZone(ZoneId.systemDefault());
        ZonedDateTime periodStart = from.atZone(ZoneId.systemDefault());
        ZonedDateTime periodEnd = to.atZone(ZoneId.systemDefault());

        // Get occurrences in the specified period
        int valueDateTime = 1;
        Iterable<? extends java.time.temporal.Temporal> occurrences = recur.getDates(start, periodStart, periodEnd, valueDateTime);

        List<DoseResponse> results = new ArrayList<>();
        for (java.time.temporal.Temporal dt : occurrences) {
            ZonedDateTime zdt = (ZonedDateTime) dt;
            LocalDateTime scheduled = zdt.toLocalDateTime();

            DoseResponse dto = doseMapper.fromRecurrence(posology, scheduled);
            results.add(dto);
        }
        return results;
    }

    public List<DoseResponse> getDosesForUserToday(Long userId) throws ParseException {
        // Define start and end of today as datetime range
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Call the generic method providing today's date range
        return getDosesForUser(userId, startOfDay, endOfDay);
    }

    public List<DoseResponse> getDosesForUser(Long userId, LocalDateTime from, LocalDateTime to) throws ParseException {
        List<Posology> posologies = posologyRepository.findByUser_Id(userId);

        List<DoseResponse> generatedDoses = new ArrayList<>();
        for (Posology posology : posologies) {
            generatedDoses.addAll(generateOccurrences(posology, from, to));
        }

        List<Dose> storedDoses = doseRepository.findByUser_IdAndScheduledDateTimeBetween(userId, from, to);

        Map<LocalDateTime, DoseResponse> doseMap = new HashMap<>();

        for (DoseResponse d : generatedDoses) {
            doseMap.put(d.scheduledDateTime(), d);
        }

        for (Dose d : storedDoses) {
            DoseResponse dto = doseMapper.toResponse(d, d.getScheduledDateTime());
            doseMap.put(dto.scheduledDateTime(), dto);
        }

        return new ArrayList<>(doseMap.values());
    }

    public Dose getDoseById(Long doseId) {
        return doseRepository.findById(doseId)
                .orElseThrow(() -> new DoseNotFoundById(doseId));
    }

    public boolean markDoseAsTaken(Long doseId) {
        Dose dose = getDoseById(doseId);
        dose.setTaken(true);
        dose.setTakenTime(LocalDateTime.now());
        doseRepository.save(dose);
        return true;
    }
}
