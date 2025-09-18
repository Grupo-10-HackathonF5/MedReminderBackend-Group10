package com.hackathon.medreminder.dose.service;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.dto.DoseMapperImpl;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.exception.DoseNotFoundById;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.user.entity.User;
import lombok.RequiredArgsConstructor;
import net.fortuna.ical4j.model.Recur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoseService {
    private final DoseRepository doseRepository;
    private final PosologyRepository posologyRepository;
    private final DoseMapper doseMapper;

    /**
     * Genera las ocurrencias virtuales usando iCal4j sin persistir
     */
    private List<LocalDateTime> generateVirtualOccurrences(Posology posology, LocalDateTime from, LocalDateTime to) throws ParseException {
        // Construir RRULE desde los campos de frecuencia de Posology
        String rrule = "FREQ=" + posology.getFrequencyUnit().name() +
                ";INTERVAL=" + posology.getFrequencyValue();
        
        if (posology.getEndDate() != null) {
            rrule += ";UNTIL=" + posology.getEndDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "T235959Z";
        }

        Recur recur = new Recur(rrule);

        // Usar el startDate de posology combinado con dayTime para el inicio
        LocalDateTime startDateTime = LocalDateTime.of(
            posology.getStartDate(), 
            posology.getDayTime().toLocalTime()
        );
        
        ZonedDateTime start = startDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime periodStart = from.atZone(ZoneId.systemDefault());
        ZonedDateTime periodEnd = to.atZone(ZoneId.systemDefault());

        // Obtener ocurrencias en el período especificado
        int valueType = 1; // VALUE=DATE-TIME
        Iterable<? extends java.time.temporal.Temporal> occurrences = 
            recur.getDates(start, periodStart, periodEnd, valueType);

        List<LocalDateTime> results = new ArrayList<>();
        for (java.time.temporal.Temporal dt : occurrences) {
            if (dt instanceof ZonedDateTime) {
                ZonedDateTime zdt = (ZonedDateTime) dt;
                LocalDateTime scheduled = zdt.toLocalDateTime();
                
                // Filtrar solo las fechas que están dentro del rango
                if (!scheduled.isBefore(from) && !scheduled.isAfter(to)) {
                    results.add(scheduled);
                }
            }
        }
        
        return results;
    }

    /**
     * Crea físicamente las dosis en la base de datos para las ocurrencias que no existen
     */
    @Transactional
    private List<Dose> createMissingDoses(Posology posology, List<LocalDateTime> scheduledTimes) {
        List<Dose> createdDoses = new ArrayList<>();
        User user = posology.getUser();
        
        for (LocalDateTime scheduledTime : scheduledTimes) {
            // Verificar si ya existe una dosis para esta fecha y posología
            Optional<Dose> existingDose = doseRepository
                .findByPosology_IdAndScheduledDateTime(posology.getId(), scheduledTime);
            
            if (existingDose.isEmpty()) {
                // Crear nueva dosis
                Dose newDose = Dose.builder()
                    .posology(posology)
                    .user(user)
                    .scheduledDateTime(scheduledTime)
                    .scheduledDay(scheduledTime.toLocalDate())
                    .isTaken(false)
                    .takenTime(null)
                    .build();
                
                Dose savedDose = doseRepository.save(newDose);
                createdDoses.add(savedDose);
            }
        }
        
        return createdDoses;
    }

    /**
     * Obtiene las dosis para un usuario en un período, creando las que falten
     */
    @Transactional
    public List<DoseResponse> getDosesForUser(Long userId, LocalDateTime from, LocalDateTime to) throws ParseException {
        List<Posology> posologies = posologyRepository.findByUser_Id(userId);

        // Crear las dosis que falten para cada posología
        for (Posology posology : posologies) {
            // Solo generar dosis si la posología está activa en el período
            if (isPosologyActiveInPeriod(posology, from, to)) {
                List<LocalDateTime> virtualOccurrences = generateVirtualOccurrences(posology, from, to);
                createMissingDoses(posology, virtualOccurrences);
            }
        }

        // Ahora obtener todas las dosis reales de la BD para el período
        List<Dose> storedDoses = doseRepository.findByUser_IdAndScheduledDateTimeBetween(userId, from, to);

        // Convertir a DTO
        return storedDoses.stream()
            .map(dose -> doseMapper.toResponse(dose))
            .collect(Collectors.toList());
    }

    /**
     * Obtiene las dosis de hoy para un usuario, creando las que falten
     */
    @Transactional
    public List<DoseResponse> getTodayDosesForUser(Long userId) throws ParseException {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        return getDosesForUser(userId, startOfDay, endOfDay);
    }

    /**
     * Verifica si una posología está activa en un período dado
     */
    private boolean isPosologyActiveInPeriod(Posology posology, LocalDateTime from, LocalDateTime to) {
        LocalDate fromDate = from.toLocalDate();
        LocalDate toDate = to.toLocalDate();
        
        // La posología debe haber empezado antes o durante el período
        if (posology.getStartDate().isAfter(toDate)) {
            return false;
        }
        
        // Si tiene fecha de fin, debe terminar después o durante el período
        if (posology.getEndDate() != null && posology.getEndDate().isBefore(fromDate)) {
            return false;
        }
        
        return true;
    }

    public Dose getDoseById(Long doseId) {
        return doseRepository.findById(doseId)
                .orElseThrow(() -> new DoseNotFoundById(doseId));
    }

    @Transactional
    public boolean markDoseAsTaken(Long doseId) {
        Dose dose = getDoseById(doseId);
        dose.setIsTaken(true);
        dose.setTakenTime(LocalDateTime.now());
        doseRepository.save(dose);
        return true;
    }

    @Transactional
    public boolean markDoseAsNotTaken(Long doseId) {
        Dose dose = getDoseById(doseId);
        dose.setIsTaken(false);
        dose.setTakenTime(null);
        doseRepository.save(dose);
        return true;
    }

    /**
     * Cambia el estado de una dosis (tomada/no tomada)
     */
    @Transactional
    public String toggleDoseStatus(Long doseId) {
        Dose dose = getDoseById(doseId);
        if (Boolean.TRUE.equals(dose.getIsTaken())) {
            markDoseAsNotTaken(doseId);
            return "Dosis marcada como no tomada.";
        } else {
            markDoseAsTaken(doseId);
            return "Dosis marcada como tomada.";
        }
    }
}