package com.hackathon.medreminder.dose.service;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.exception.DoseNotFoundById;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoseService {
    private final DoseRepository doseRepository;
    private final DoseMapper doseMapper;

    public List<DoseResponse> getAllDosesForUser(Long userId) {
        List<Dose> storedDoses = doseRepository.findByUser_Id(userId);
        return storedDoses.stream()
                .map(dose -> doseMapper.toResponse(dose))
                .collect(Collectors.toList());
    }

    public List<DoseResponse> getDosesForUser(Long userId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Dose> doses = doseRepository.findByUser_IdAndScheduledDateTimeBetweenOrderByScheduledDateTimeAsc(userId, fromDateTime, toDateTime);
        return doses.stream()
                .map(doseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public Dose getDoseById(Long doseId) {
        return doseRepository.findById(doseId)
                .orElseThrow(() -> new DoseNotFoundById(doseId));
    }

    @Transactional
    public boolean markDoseAsTaken(Long doseId) {
        Dose dose = getDoseById(doseId);
        dose.setIsTaken(true); // Usar el nombre correcto del campo
        dose.setTakenTime(LocalDateTime.now());
        doseRepository.save(dose);
        return true;
    }

    @Transactional
    public boolean markDoseAsNotTaken(Long doseId) {
        Dose dose = getDoseById(doseId);
        dose.setIsTaken(false); // Usar el nombre correcto del campo
        dose.setTakenTime(null);
        doseRepository.save(dose);
        return true;
    }

    @Transactional
    public String toggleDoseStatus(Long doseId) {
        Dose dose = getDoseById(doseId);
        if (Boolean.TRUE.equals(dose.getIsTaken())) { // Usar el getter correcto
            markDoseAsNotTaken(doseId);
            return "Dosis marcada como no tomada.";
        } else {
            markDoseAsTaken(doseId);
            return "Dosis marcada como tomada.";
        }
    }
}