package com.hackathon.medreminder.posology;

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
    private  User user;

    @BeforeEach
    void setUp() {
        Medication medication = Medication.builder().id(10L).build();
        user = User.builder()
                .id(1L).build();
        posology = Posology.builder()
                .id(1L)
                .medication(medication)
                .startDate(LocalDate.now())
                .user(user)
                .dayTime(LocalDate.now().atStartOfDay())
                .frequencyValue(3)
                .frequencyUnit(FrequencyUnit.HOURLY)
                .quantity(5.0)
                .reminderMessage("Take with food")
                .dosesNumber(10.0)
                .build();


        posologyRequest = new PosologyRequest(
                10L, user.getId(), LocalDate.now(), LocalDate.now(), LocalDate.now().atStartOfDay(),
                3, FrequencyUnit.HOURLY, 5.0, "Take with food", 10.0);

        posologyResponse = new PosologyResponse(1L, 10L, "medicationName", LocalDate.now(), null,
                LocalDate.now().atStartOfDay(), 3, null, 5.0,
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
        verify(posologyMapper).toResponse(posology);
    }

    @Test
    void getPosologyById_notFound_throws() {
        when(posologyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(PosologyNotFoundById.class, () -> posologyService.getPosologyById(999L));
    }

    @Test
    void getPosologiesByMedicationId_returnsMapped() {
        List<Posology> entities = List.of(posology);
        List<PosologyResponse> dtos = List.of(posologyResponse);

        when(posologyRepository.findByMedicationId(posology.getMedication().getId())).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getPosologiesByMedicationId(posology.getMedication().getId());

        assertEquals(dtos, result);
        verify(posologyRepository).findByMedicationId(posology.getMedication().getId());
        verify(entityMapperUtil).mapEntitiesToDTOs(eq(entities), any());
    }

    @Test
    void getActivePosologies_returnsMapped() {
        List<Posology> entities = List.of(posology);
        List<PosologyResponse> dtos = List.of(posologyResponse);

        when(posologyRepository.findActivePosologies(any())).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getActivePosologies();

        assertEquals(dtos, result);
        verify(posologyRepository).findActivePosologies(any());
        verify(entityMapperUtil).mapEntitiesToDTOs(eq(entities), any());
    }

    @Test
    void createPosology_savesAndMaps() {
        when(userService.getUserEntityById(user.getId())).thenReturn(user);
        when(medicationService.getMedicationEntityById(posologyRequest.medicationId())).thenReturn(medication);
        when(posologyMapper.toPosology(posologyRequest)).thenReturn(posology);
        when(posologyRepository.save(posology)).thenReturn(posology);
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse response = posologyService.createPosology(posologyRequest);

        assertEquals(posologyResponse, response);
        verify(medicationService).getMedicationEntityById(posologyRequest.medicationId());
        verify(posologyMapper).toPosology(posologyRequest);
        verify(posologyRepository).save(posology);
        verify(posologyMapper).toResponse(posology);
    }

    @Test
    void updatePosology_updatesAndMaps() {
        Posology updatedPosology = Posology.builder()
                .id(posology.getId())
                .medication(medication)
                .startDate(posologyRequest.startDate())
                .endDate(posologyRequest.endDate())
                .dayTime(posologyRequest.dayTime())
                .frequencyValue(posologyRequest.frequencyValue())
                .frequencyUnit(posologyRequest.frequencyUnit())
                .quantity(posologyRequest.quantity())
                .reminderMessage(posologyRequest.reminderMessage())
                .dosesNumber(posologyRequest.dosesNumber())
                .user(posology.getUser())
                .build();

        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        when(medicationService.getMedicationEntityById(posologyRequest.medicationId())).thenReturn(medication);
        when(posologyRepository.save(any(Posology.class))).thenReturn(updatedPosology);
        when(posologyMapper.toResponse(any(Posology.class))).thenReturn(posologyResponse);

        PosologyResponse response = posologyService.updatePosology(posology.getId(), posologyRequest);

        assertEquals(posologyResponse, response);
        verify(posologyRepository).findById(posology.getId());
        verify(medicationService).getMedicationEntityById(posologyRequest.medicationId());
        verify(posologyRepository).save(any(Posology.class));
        verify(posologyMapper).toResponse(any(Posology.class));
    }


    @Test
    void deletePosology_deletesAndReturnsMessage() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        doNothing().when(posologyRepository).deleteById(posology.getId());

        String message = posologyService.deletePosology(posology.getId());

        assertEquals(String.format("Posology from %s deleted correctly", posology.getMedication().getName()), message);
        verify(posologyRepository).findById(posology.getId());
        verify(posologyRepository).deleteById(posology.getId());
    }
}
