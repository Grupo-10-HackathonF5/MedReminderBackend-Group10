package com.hackathon.medreminder.dose;

import com.hackathon.medreminder.dose.dto.DoseMapper;
import com.hackathon.medreminder.dose.dto.DoseResponse;
import com.hackathon.medreminder.dose.entity.Dose;
import com.hackathon.medreminder.dose.repository.DoseRepository;
import com.hackathon.medreminder.dose.service.DoseService;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoseServiceTest {

    @Mock
    private DoseRepository doseRepository;

    @Mock
    private DoseMapper doseMapper;

    @InjectMocks
    private DoseService doseService;

    private User user;
    private Posology hourlyPosology;
    private Posology dailyPosology;
    private Posology weeklyPosology;
    private Posology monthlyPosology;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        hourlyPosology = Posology.builder()
                .id(1L)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .dayTime(LocalDateTime.now().withHour(8).withMinute(0).toLocalTime())
                .frequencyValue(8)
                .frequencyUnit(FrequencyUnit.HOURLY)
                .quantity(1.0)
                .reminderMessage("Take every 8 hours")
                .dosesNumber(21.0)
                .build();

        dailyPosology = Posology.builder()
                .id(2L)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(2))
                .dayTime(LocalDateTime.now().withHour(9).withMinute(0).toLocalTime())
                .frequencyValue(2)
                .frequencyUnit(FrequencyUnit.DAILY)
                .quantity(1.0)
                .reminderMessage("Take every 2 days")
                .dosesNumber(7.0)
                .build();

        weeklyPosology = Posology.builder()
                .id(3L)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusWeeks(10))
                .dayTime(LocalDateTime.now().withHour(10).withMinute(0).toLocalTime())
                .frequencyValue(1)
                .frequencyUnit(FrequencyUnit.WEEKLY)
                .quantity(1.0)
                .reminderMessage("Take weekly")
                .dosesNumber(10.0)
                .build();

        monthlyPosology = Posology.builder()
                .id(4L)
                .user(user)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(5))
                .dayTime(LocalDateTime.now().withHour(11).withMinute(0).toLocalTime())
                .frequencyValue(1)
                .frequencyUnit(FrequencyUnit.MONTHLY)
                .quantity(1.0)
                .reminderMessage("Take monthly")
                .dosesNumber(5.0)
                .build();
    }

    @Test
    void getDoseById_returnsDoseWhenFound() {
        Dose mockDose = createMockDose();
        when(doseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDose));

        Dose result = doseService.getDoseById(1L);

        assertEquals(mockDose.getId(), result.getId());
        verify(doseRepository).findById(1L);
    }

    @Test
    void markDoseAsTaken_changesStatusToTaken() {
        Dose mockDose = createMockDose();
        when(doseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDose));
        when(doseRepository.save(any(Dose.class))).thenReturn(mockDose);

        boolean result = doseService.markDoseAsTaken(1L);

        assertTrue(result);
        assertTrue(mockDose.getIsTaken());
        verify(doseRepository).save(mockDose);
    }

    @Test
    void toggleDoseStatus_changesFromNotTakenToTaken() {
        Dose mockDose = createMockDose();
        mockDose.setIsTaken(false);
        when(doseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDose));
        when(doseRepository.save(any(Dose.class))).thenReturn(mockDose);

        String result = doseService.toggleDoseStatus(1L);

        assertEquals("Dosis marcada como tomada.", result);
        assertTrue(mockDose.getIsTaken());
        verify(doseRepository).save(mockDose);
    }

    @Test
    void toggleDoseStatus_changesFromTakenToNotTaken() {
        Dose mockDose = createMockDose();
        mockDose.setIsTaken(true);
        mockDose.setTakenTime(LocalDateTime.now());
        when(doseRepository.findById(1L)).thenReturn(java.util.Optional.of(mockDose));
        when(doseRepository.save(any(Dose.class))).thenReturn(mockDose);

        String result = doseService.toggleDoseStatus(1L);

        assertEquals("Dosis marcada como no tomada.", result);
        assertFalse(mockDose.getIsTaken());
        assertNull(mockDose.getTakenTime());
        verify(doseRepository).save(mockDose);
    }

    private Dose createMockDose() {
        return Dose.builder()
                .id(1L)
                .posology(hourlyPosology)
                .user(user)
                .scheduledDateTime(LocalDateTime.now())
                .scheduledDay(LocalDate.now())
                .isTaken(false)
                .takenTime(null)
                .build();
    }

    private DoseResponse createMockDoseResponse() {
        return DoseResponse.builder()
                .doseId(1L)
                .posologyId(1L)
                .medicationName("Test Medication")
                .dosesNumber(2.0)
                .medicationId(2L)
                .scheduledDay(LocalDate.now())
                .scheduledDateTime(LocalDateTime.now())
                .isTaken(false)
                .takenTime(null)
                .build();
    }
}
