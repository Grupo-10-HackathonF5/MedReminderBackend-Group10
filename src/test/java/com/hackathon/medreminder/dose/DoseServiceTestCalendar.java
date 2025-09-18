package com.hackathon.medreminder.dose;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.service.DoseService;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoseServiceTestCalendar {

    @Mock
    private DoseRepository doseRepository;

    @Mock
    private PosologyRepository posologyRepository;

    @Mock
    private DoseMapper doseMapper;

    @InjectMocks
    private DoseService doseService;

    private User user;
    private Posology posology;
    private Dose dose;
    private DoseResponse doseResponse;
    private Medication medication;

    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();

        medication = Medication.builder()
                .name("Ibupofren").build();

        // Fix for dayTime as LocalDateTime variable, so use LocalDateTime.of for test
        posology = Posology.builder()
                .id(1L)
                .user(user)
                .frequencyUnit(FrequencyUnit.DAYS)
                .medication(medication)
                .frequencyValue(1)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(null)
                .dayTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)))
                .build();

        dose = Dose.builder()
                .id(1L)
                .posology(posology)
                .user(user)
                .scheduledDateTime(LocalDateTime.now())
                .scheduledDay(LocalDate.now())
                .isTaken(false)
                .takenTime(null)
                .build();

        doseResponse = DoseResponse.builder()
                .doseId(dose.getId())
                .posologyId(posology.getId())
                .medicationName(posology.getMedication().getName())
                .dosesNumber(posology.getDosesNumber())
                .medicationId(posology.getMedication().getId())
                .scheduledDay(dose.getScheduledDay())
                .scheduledDateTime(dose.getScheduledDateTime())
                .isTaken(dose.getIsTaken())
                .takenTime(dose.getTakenTime())
                .build();


        fromDateTime = LocalDateTime.now().minusDays(1);
        toDateTime = LocalDateTime.now().plusDays(1);
    }

    @Test
    void mapFrequencyUnitToICalFreq_withValidUnits() {
        assertEquals("HOURLY", doseService.mapFrequencyUnitToICalFreq(FrequencyUnit.HOURS));
        assertEquals("HOURLY", doseService.mapFrequencyUnitToICalFreq(FrequencyUnit.HOURLY));
        assertEquals("DAILY", doseService.mapFrequencyUnitToICalFreq(FrequencyUnit.DAYS));
        assertEquals("WEEKLY", doseService.mapFrequencyUnitToICalFreq(FrequencyUnit.WEEKS));
        assertEquals("MONTHLY", doseService.mapFrequencyUnitToICalFreq(FrequencyUnit.MONTHS));
    }

    @Test
    void isPosologyActiveInPeriod_whenActive() {
        LocalDateTime from = LocalDateTime.now().minusDays(3);
        LocalDateTime to = LocalDateTime.now().plusDays(3);

        assertTrue(doseService.isPosologyActiveInPeriod(posology, from, to));
    }

    @Test
    void isPosologyActiveInPeriod_startDateAfterTo_false() {
        LocalDateTime from = LocalDateTime.now().minusDays(2);
        LocalDateTime to = LocalDateTime.now();

        Posology futurePosology = Posology.builder()
                .id(1L)
                .user(user)
                .frequencyUnit(FrequencyUnit.DAYS)
                .medication(medication)
                .frequencyValue(1)
                .startDate(to.toLocalDate().plusDays(1))
                .endDate(null)
                .dayTime(LocalDateTime.of(to.toLocalDate(), LocalTime.of(8, 0)))
                .build();

        assertFalse(doseService.isPosologyActiveInPeriod(futurePosology, from, to));
    }


    @Test
    void isPosologyActiveInPeriod_endDateBeforeFrom_false() {
        Posology endedPosology = Posology.builder()
                .id(2L)
                .user(user)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(5))
                .dayTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0)))
                .frequencyUnit(FrequencyUnit.DAYS)
                .frequencyValue(1)
                .build();

        LocalDateTime from = LocalDateTime.now().minusDays(4);
        LocalDateTime to = LocalDateTime.now();

        assertFalse(doseService.isPosologyActiveInPeriod(endedPosology, from, to));
    }

    @Test
    void generateVirtualOccurrences_returnsOccurrencesWithinRange() throws ParseException {
        // Test basic valid generation for Posology with no end date
        List<LocalDateTime> occurrences = doseService.generateVirtualOccurrences(posology, fromDateTime, toDateTime);

        assertNotNull(occurrences);
        assertFalse(occurrences.isEmpty());

        for (LocalDateTime occurrence : occurrences) {
            assertFalse(occurrence.isBefore(fromDateTime));
            assertFalse(occurrence.isAfter(toDateTime));
        }
    }

    @Test
    void createMissingDoses_createsAndSavesNewDoses() {
        List<LocalDateTime> scheduledTimes = List.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(doseRepository.findByPosology_IdAndScheduledDateTime(posology.getId(), scheduledTimes.get(0)))
                .thenReturn(Optional.empty());
        when(doseRepository.findByPosology_IdAndScheduledDateTime(posology.getId(), scheduledTimes.get(1)))
                .thenReturn(Optional.of(dose)); // Existing dose for second date

        when(doseRepository.save(any(Dose.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Dose> createdDoses = doseService.createMissingDoses(posology, scheduledTimes);

        // Only one new dose should be created (for the first date)
        assertEquals(1, createdDoses.size());
        verify(doseRepository, times(1)).save(any(Dose.class));
    }

    @Test
    void getDosesForUser_createsMissingDosesAndReturnsResponses() throws ParseException {
        List<Posology> posologies = List.of(posology);
        List<Dose> storedDoses = List.of(dose);
        List<DoseResponse> doseResponses = List.of(doseResponse);

        when(posologyRepository.findByUser_Id(user.getId())).thenReturn(posologies);
        when(doseRepository.findByUser_IdAndScheduledDateTimeBetween(user.getId(), fromDateTime, toDateTime)).thenReturn(storedDoses);
        when(doseRepository.findByPosology_IdAndScheduledDateTime(anyLong(), any(LocalDateTime.class))).thenReturn(Optional.empty());
        when(doseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(doseMapper.toResponse(any(Dose.class))).thenReturn(doseResponse);

        List<DoseResponse> responses = doseService.getDosesForUser(user.getId(), fromDateTime, toDateTime);

        assertEquals(doseResponses.size(), responses.size());
        verify(posologyRepository).findByUser_Id(user.getId());
        verify(doseRepository).findByUser_IdAndScheduledDateTimeBetween(user.getId(), fromDateTime, toDateTime);
        verify(doseMapper, atLeastOnce()).toResponse(any(Dose.class));
    }

    @Test
    void getTodayDosesForUser_callsGetDosesForUserWithTodayRange() throws ParseException {
        DoseService spyService = Mockito.spy(doseService);

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        List<DoseResponse> expectedResponses = List.of(doseResponse);

        doReturn(expectedResponses).when(spyService).getDosesForUser(anyLong(), eq(startOfDay), eq(endOfDay));

        List<DoseResponse> responses = spyService.getTodayDosesForUser(user.getId());

        assertEquals(expectedResponses, responses);
        verify(spyService).getDosesForUser(user.getId(), startOfDay, endOfDay);
    }
}
