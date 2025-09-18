package com.hackathon.medreminder.posology;

import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.service.MedicationService;
import com.hackathon.medreminder.posology.dto.PosologyMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.entity.Posology;
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
    MedicationService medicationService;

    @Mock
    UserService userService;

    @Mock
    private PosologyMapper posologyMapper;

    @Mock
    private EntityMapperUtil entityMapperUtil;

    @InjectMocks
    private PosologyService posologyService;

    private Posology posology;
    private PosologyRequest posologyRequest;
    private PosologyResponse posologyResponse;
    private Medication medication;
    private User user;

    @BeforeEach
    void setUp() {
        medication = Medication.builder().id(10L).build();
        user = User.builder()
                .id(1L).build();
        posology = Posology.builder()
                .id(1L)
                .medication(medication)
                .startDate(LocalDate.now())
                .user(user)
                .dayTime(LocalDate.now().atStartOfDay())
                .frequencyValue(8)
                .frequencyUnit(FrequencyUnit.HOURS) // Cambio aquí: HOURLY -> HOURS
                .quantity(5.0)
                .reminderMessage("Take with food")
                .dosesNumber(10.0)
                .build();

        posologyRequest = new PosologyRequest(
                10L, user.getId(), LocalDate.now(), LocalDate.now(), LocalDate.now().atStartOfDay(),
                8, FrequencyUnit.HOURS, 5.0, "Take with food", 10.0); // Cambio aquí: HOURLY -> HOURS

        posologyResponse = new PosologyResponse(1L, 10L, "medicationName", LocalDate.now(), null,
                LocalDate.now().atStartOfDay(), 8, FrequencyUnit.HOURS, 5.0, // Cambio aquí: HOURLY -> HOURS
                "Take with food", 10.0);
    }

    @Test
    void getAllPosologies_returnsMappedList() {
        List<Posology> entities = List.of(posology);
        List<PosologyResponse> dtos = List.of(posologyResponse);

        when(posologyRepository.findAll()).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getAllPosologies();

        assertEquals(dtos, result);
        verify(posologyRepository).findAll();
        verify(entityMapperUtil).mapEntitiesToDTOs(eq(entities), any());
    }

    @Test
    void getPosologyById_found() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse response = posologyService.getPosologyById(posology.getId());

        assertEquals(posologyResponse, response);
        verify(posologyRepository).findById(posology.getId());
    }
}