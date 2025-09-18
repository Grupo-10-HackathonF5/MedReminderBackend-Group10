package com.hackathon.medreminder.posology;

import com.hackathon.medreminder.dose.service.DoseSchedulerService;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.service.MedicationService;
import com.hackathon.medreminder.posology.dto.PosologyMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.posology.service.PosologyService;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosologyServiceTest {

    @Mock
    private PosologyRepository posologyRepository;

    @Mock
    private PosologyMapper posologyMapper;

    @Mock
    private EntityMapperUtil entityMapperUtil;

    @Mock
    private MedicationService medicationService;

    @Mock
    private UserService userService;

    @Mock
    private DoseSchedulerService doseSchedulerService;

    @InjectMocks
    private PosologyService posologyService;

    private User user;
    private Medication medication;
    private Posology posology;
    private PosologyRequest posologyRequest;
    private PosologyResponse posologyResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(42L)
                .username("testuser")
                .build();

        medication = Medication.builder()
                .id(1L)
                .name("Ibuprofen")
                .build();

        posology = Posology.builder()
                .id(100L)
                .user(user)
                .medication(medication)
                .startDate(LocalDate.now())
                .endDate(null)
                .frequencyValue(1)
                .frequencyUnit(FrequencyUnit.DAYS)
                .quantity(2.0)
                .reminderMessage("Take after breakfast")
                .dosesNumber(10.0)
                .build();

        posologyRequest = PosologyRequest.builder()
                .medicationId(medication.getId())
                .userId(user.getId())
                .startDate(LocalDate.now())
                .endDate(null)  // if allowed, otherwise set a valid LocalDate
                .dayTime(LocalDateTime.of(2025, 9, 18, 8, 0).toLocalTime()) // sample time
                .frequencyValue(1)
                .frequencyUnit(FrequencyUnit.DAYS)
                .quantity(2.0)
                .reminderMessage("Take after breakfast")
                .dosesNumber(10.0)
                .build();

        posologyResponse = new PosologyResponse(
                posology.getId(),
                medication.getId(),
                medication.getName(),
                posology.getStartDate(),
                posology.getEndDate(),
                posology.getDayTime(),
                posology.getFrequencyValue(),
                posology.getFrequencyUnit(),
                posology.getQuantity(),
                posology.getReminderMessage(),
                posology.getDosesNumber()
        );
    }

    @Test
    void getAllPosologies_returnsMappedList() {
        List<Posology> entities = List.of(posology);
        List<PosologyResponse> responses = List.of(posologyResponse);

        when(posologyRepository.findAll()).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getAllPosologies();

        assertEquals(responses, result);
        verify(posologyRepository).findAll();
        verify(entityMapperUtil).mapEntitiesToDTOs(eq(entities), any());
    }

    @Test
    void getPosologyById_found() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse result = posologyService.getPosologyById(posology.getId());

        assertEquals(posologyResponse, result);
        verify(posologyRepository).findById(posology.getId());
        verify(posologyMapper).toResponse(posology);
    }

    @Test
    void getPosologyById_notFound_throws() {
        when(posologyRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(PosologyNotFoundById.class, () -> posologyService.getPosologyById(999L));
    }

    @Test
    void getActivePosologiesByUserId_returnsValidList() {
        List<Posology> entities = List.of(posology);
        List<PosologyResponse> responses = List.of(posologyResponse);

        when(userService.getUserEntityById(user.getId())).thenReturn(user);
        when(posologyRepository.findByUser_IdAndEndDateIsNullOrEndDateAfter(eq(user.getId()), any(LocalDate.class))).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getActivePosologiesByUserId(user.getId());

        assertEquals(responses, result);
        verify(userService).getUserEntityById(user.getId());
    }

    @Test
    void createPosology_savesAndReturnsResponse() {
        when(medicationService.getMedicationEntityById(posologyRequest.medicationId())).thenReturn(medication);
        when(userService.getUserEntityById(posologyRequest.userId())).thenReturn(user);
        when(posologyMapper.toPosology(posologyRequest)).thenReturn(posology);
        when(posologyRepository.save(posology)).thenReturn(posology);
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse result = posologyService.createPosology(posologyRequest);

        assertEquals(posologyResponse, result);
        verify(medicationService).getMedicationEntityById(posologyRequest.medicationId());
        verify(userService).getUserEntityById(posologyRequest.userId());
        verify(posologyRepository).save(posology);
        verify(doseSchedulerService).scheduleDosesForPosology(posology);
    }

    @Test
    void updatePosology_updatesEntityAndReturnsResponse() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        when(medicationService.getMedicationEntityById(posologyRequest.medicationId())).thenReturn(medication);
        when(posologyRepository.save(any(Posology.class))).thenReturn(posology);
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse result = posologyService.updatePosology(posology.getId(), posologyRequest);

        assertEquals(posologyResponse, result);
        verify(posologyRepository).findById(posology.getId());
        verify(posologyRepository).save(posology);
    }

    @Test
    void deletePosology_deletesAndReturnsMessage() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        doNothing().when(posologyRepository).deleteById(posology.getId());

        String result = posologyService.deletePosology(posology.getId());

        assertTrue(result.contains("deleted correctly"));
        verify(posologyRepository).deleteById(posology.getId());
    }
}
