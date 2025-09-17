package com.hackathon.medreminder.posology;

import com.hackathon.medreminder.posology.dto.PosologyMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.posology.service.PosologyService;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
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
    private PosologyMapper posologyMapper;

    @Mock
    private EntityMapperUtil entityMapperUtil;

    @InjectMocks
    private PosologyService posologyService;

    private Posology posology;
    private PosologyRequest posologyRequest;
    private PosologyResponse posologyResponse;

    @BeforeEach
    void setUp() {
        posology = new Posology(1L, 10L, LocalDate.now(), null, LocalDate.now().atStartOfDay(),
                3, null, 5.0, "Take with food", 10.0);

        posologyRequest = new PosologyRequest(
                10L, LocalDate.now(), LocalDate.now(), LocalDate.now().atStartOfDay(),
                3, FrequencyUnit.HOUR, 5.0, "Take with food", 10.0);

        posologyResponse = new PosologyResponse(1L, 10L, LocalDate.now(), null,
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

        when(posologyRepository.findByMedicationId(posology.getMedicationId())).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(posologyResponse));

        List<PosologyResponse> result = posologyService.getPosologiesByMedicationId(posology.getMedicationId());

        assertEquals(dtos, result);
        verify(posologyRepository).findByMedicationId(posology.getMedicationId());
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
        when(posologyMapper.toPosology(posologyRequest)).thenReturn(posology);
        when(posologyRepository.save(posology)).thenReturn(posology);
        when(posologyMapper.toResponse(posology)).thenReturn(posologyResponse);

        PosologyResponse response = posologyService.createPosology(posologyRequest);

        assertEquals(posologyResponse, response);
        verify(posologyMapper).toPosology(posologyRequest);
        verify(posologyRepository).save(posology);
        verify(posologyMapper).toResponse(posology);
    }

    @Test
    void updatePosology_updatesAndMaps() {
        Posology updatedPosology = new Posology(posology.getId(), posologyRequest.medicationId(), posologyRequest.startDate(), posologyRequest.endDate(),
                posologyRequest.dayTime(), posologyRequest.frequencyValue(), posologyRequest.frequencyUnit(), posologyRequest.quantity(),
                posologyRequest.reminderMessage(), posologyRequest.dosesNumber());

        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        when(posologyRepository.save(any(Posology.class))).thenReturn(updatedPosology);
        when(posologyMapper.toResponse(any(Posology.class))).thenReturn(posologyResponse);

        PosologyResponse response = posologyService.updatePosology(posology.getId(), posologyRequest);

        assertEquals(posologyResponse, response);
        verify(posologyRepository).findById(posology.getId());
        verify(posologyRepository).save(any(Posology.class));
        verify(posologyMapper).toResponse(any(Posology.class));
    }

    @Test
    void deletePosology_deletesAndReturnsMessage() {
        when(posologyRepository.findById(posology.getId())).thenReturn(Optional.of(posology));
        doNothing().when(posologyRepository).deleteById(posology.getId());

        String message = posologyService.deletePosology(posology.getId());

        assertEquals(String.format("Posology from %s deleted correctly", posology.getMedicationId()), message);
        verify(posologyRepository).findById(posology.getId());
        verify(posologyRepository).deleteById(posology.getId());
    }
}
