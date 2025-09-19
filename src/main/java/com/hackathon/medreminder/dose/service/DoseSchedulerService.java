package com.hackathon.medreminder.dose.service;

import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoseSchedulerService {

    private final DoseRepository doseRepository;

    @Transactional
    public List<Dose> scheduleDosesForPosology(Posology posology) {
        List<Dose> createdDoses = new ArrayList<>();
        User user = posology.getUser();

        LocalDateTime nextDoseDateTime = getInitialDoseDateTime(posology);
        LocalDateTime endDateTime = getEndDateTime(posology, nextDoseDateTime);

        while (!nextDoseDateTime.isAfter(endDateTime)) {
            if (!doseExists(posology.getId(), nextDoseDateTime)) {
                Dose newDose = buildDose(posology, user, nextDoseDateTime);
                createdDoses.add(doseRepository.save(newDose));
            }
            nextDoseDateTime = getNextDoseDateTime(nextDoseDateTime, posology);
        }

        return createdDoses;
    }

    private LocalDateTime getInitialDoseDateTime(Posology posology) {
        return LocalDateTime.of(posology.getStartDate(), posology.getDayTime());
    }

    private LocalDateTime getEndDateTime(Posology posology, LocalDateTime startDateTime) {
        return posology.getEndDate() == null
                ? startDateTime.plusYears(1)
                : posology.getEndDate().atTime(23, 59, 59);
    }

    private boolean doseExists(Long posologyId, LocalDateTime scheduledDateTime) {
        return doseRepository.findByPosology_IdAndScheduledDateTime(posologyId, scheduledDateTime).isPresent();
    }

    private Dose buildDose(Posology posology, User user, LocalDateTime scheduledDateTime) {
        return Dose.builder()
                .posology(posology)
                .user(user)
                .scheduledDateTime(scheduledDateTime)
                .scheduledDay(scheduledDateTime.toLocalDate())
                .isTaken(false)
                .takenTime(null)
                .build();
    }

    private LocalDateTime getNextDoseDateTime(LocalDateTime currentDoseDateTime, Posology posology) {
        FrequencyUnit unit = posology.getFrequencyUnit();
        int value = posology.getFrequencyValue();

        return switch (unit) {
            case HOURS -> currentDoseDateTime.plusHours(value);
            case DAYS -> currentDoseDateTime.plusDays(value);
            case WEEKS -> currentDoseDateTime.plusWeeks(value);
            case MONTHS -> currentDoseDateTime.plusMonths(value);
            default -> throw new IllegalArgumentException("Unsupported frequency unit: " + unit);
        };
    }
}
