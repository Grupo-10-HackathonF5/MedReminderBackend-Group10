package com.hackathon.medreminder.dose.service;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.exception.DoseNotFoundById;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DoseService {
    private final DoseRepository doseRepository;
    private final PosologyRepository posologyRepository;
    private final DoseMapper doseMapper;

    public List<DoseResponse> generateOccurrences(Posology posology, LocalDateTime from, LocalDateTime to) {
        List<DoseResponse> results = new ArrayList<>();

        LocalDateTime current = posology.getDayTime();

        // Adjust start time to be within the requested range
        while (current.isBefore(from)) {
            current = addFrequencyInterval(current, posology);
        }

        // Generate occurrences within the range
        while (!current.isAfter(to) &&
                (posology.getEndDate() == null || !current.toLocalDate().isAfter(posology.getEndDate()))) {

            DoseResponse dto = doseMapper.fromRecurrence(posology, current);
            results.add(dto);

            current = addFrequencyInterval(current, posology);
        }

        return results;
    }

    private LocalDateTime addFrequencyInterval(LocalDateTime dateTime, Posology posology) {
        int value = posology.getFrequencyValue();
        FrequencyUnit unit = posology.getFrequencyUnit();

        switch (unit) {
            case HOUR:
                return dateTime.plusHours(value);
            case DAY:
                return dateTime.plusDays(value);
            case WEEK:
                return dateTime.plusWeeks(value);
            case MONTH:
                return dateTime.plusMonths(value);
            default:
                throw new IllegalArgumentException("Unsupported frequency unit: " + unit);
        }
    }

    public List<DoseResponse> getDosesForUser(Long userId, LocalDateTime from, LocalDateTime to) {
        List<Posology> posologies = posologyRepository.findByUser_Id(userId);

        List<DoseResponse> generatedDoses = new ArrayList<>();
        for (Posology posology : posologies) {
            generatedDoses.addAll(generateOccurrences(posology, from, to));
        }

        List<Dose> storedDoses = doseRepository.findByUser_IdAndScheduledDateTimeBetween(userId, from, to);

        Map<String, DoseResponse> doseMap = new HashMap<>();

        // Add generated doses
        for (DoseResponse d : generatedDoses) {
            String key = d.posologyId() + "_" + d.scheduledDateTime().toString();
            doseMap.put(key, d);
        }

        // Override with stored doses (taken status)
        for (Dose d : storedDoses) {
            DoseResponse dto = doseMapper.toResponse(d, d.getScheduledDateTime());
            String key = d.getPosology().getId() + "_" + d.getScheduledDateTime().toString();
            doseMap.put(key, dto);
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