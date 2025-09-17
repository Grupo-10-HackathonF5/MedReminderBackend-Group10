package com.hackathon.medreminder.posology.service;

import com.hackathon.medreminder.posology.dto.PosologyDTO;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.enums.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosologyServiceTest {

    @Mock
    private PosologyRepository posologyRepository;

    @InjectMocks
    private PosologyService posologyService;

    private Posology posology;
    private PosologyDTO posologyDTO;

    @BeforeEach
    void setUp() {
        posology = new Posology(1L, 100L, LocalDate.of(2025, 9, 17), 
                LocalDate.of(2025, 10, 17), LocalDateTime.of(2025, 9, 17, 8, 0),
                8, FrequencyUnit.HOUR, 1.0, "Tomar con comida", 90.0);

        posologyDTO = new PosologyDTO(1L, 100L, LocalDate.of(2025, 9, 17), 
                LocalDate.of(2025, 10, 17), LocalDateTime.of(2025, 9, 17, 8, 0),
                8, FrequencyUnit.HOUR, 1.0, "Tomar con comida", 90.0);
    }

    @Test
    void testGetAllPosologies() {
        // Given
        when(posologyRepository.findAll()).thenReturn(Arrays.asList(posology));

        // When
        List<PosologyDTO> result = posologyService.getAllPosologies();

        // Then
        assertEquals(1, result.size());
        assertEquals(posologyDTO.getId(), result.get(0).getId());
        assertEquals(posologyDTO.getMedicationId(), result.get(0).getMedicationId());
        verify(posologyRepository).findAll();
    }

    @Test
    void testGetPosologyById_Found() {
        // Given
        when(posologyRepository.findById(1L)).thenReturn(Optional.of(posology));

        // When
        Optional<PosologyDTO> result = posologyService.getPosologyById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(posologyDTO.getId(), result.get().getId());
        assertEquals(posologyDTO.getMedicationId(), result.get().getMedicationId());
        verify(posologyRepository).findById(1L);
    }

    @Test
    void testGetPosologyById_NotFound() {
        // Given
        when(posologyRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<PosologyDTO> result = posologyService.getPosologyById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(posologyRepository).findById(1L);
    }

    @Test
    void testGetPosologiesByMedicationId() {
        // Given
        when(posologyRepository.findByMedicationId(100L)).thenReturn(Arrays.asList(posology));

        // When
        List<PosologyDTO> result = posologyService.getPosologiesByMedicationId(100L);

        // Then
        assertEquals(1, result.size());
        assertEquals(posologyDTO.getMedicationId(), result.get(0).getMedicationId());
        verify(posologyRepository).findByMedicationId(100L);
    }

    @Test
    void testGetActivePosologies() {
        // Given
        when(posologyRepository.findActivePosologies(any(LocalDate.class)))
                .thenReturn(Arrays.asList(posology));

        // When
        List<PosologyDTO> result = posologyService.getActivePosologies();

        // Then
        assertEquals(1, result.size());
        assertEquals(posologyDTO.getId(), result.get(0).getId());
        verify(posologyRepository).findActivePosologies(any(LocalDate.class));
    }

    @Test
    void testCreatePosology() {
        // Given
        PosologyDTO inputDTO = new PosologyDTO(null, 100L, LocalDate.of(2025, 9, 17), 
                LocalDate.of(2025, 10, 17), LocalDateTime.of(2025, 9, 17, 8, 0),
                8, FrequencyUnit.HOUR, 1.0, "Tomar con comida", 90.0);
        
        when(posologyRepository.save(any(Posology.class))).thenReturn(posology);

        // When
        PosologyDTO result = posologyService.createPosology(inputDTO);

        // Then
        assertEquals(posologyDTO.getId(), result.getId());
        assertEquals(posologyDTO.getMedicationId(), result.getMedicationId());
        verify(posologyRepository).save(any(Posology.class));
    }

    @Test
    void testUpdatePosology_Found() {
        // Given
        PosologyDTO updateDTO = new PosologyDTO(1L, 200L, LocalDate.of(2025, 9, 20), 
                LocalDate.of(2025, 10, 20), LocalDateTime.of(2025, 9, 20, 10, 0),
                12, FrequencyUnit.HOUR, 2.0, "Tomar sin comida", 60.0);
        
        Posology updatedPosology = new Posology(1L, 200L, LocalDate.of(2025, 9, 20), 
                LocalDate.of(2025, 10, 20), LocalDateTime.of(2025, 9, 20, 10, 0),
                12, FrequencyUnit.HOUR, 2.0, "Tomar sin comida", 60.0);
        
        when(posologyRepository.findById(1L)).thenReturn(Optional.of(posology));
        when(posologyRepository.save(any(Posology.class))).thenReturn(updatedPosology);

        // When
        Optional<PosologyDTO> result = posologyService.updatePosology(1L, updateDTO);

        // Then
        assertTrue(result.isPresent());
        assertEquals(200L, result.get().getMedicationId());
        assertEquals(12, result.get().getFrequencyValue());
        verify(posologyRepository).findById(1L);
        verify(posologyRepository).save(any(Posology.class));
    }

    @Test
    void testUpdatePosology_NotFound() {
        // Given
        PosologyDTO updateDTO = new PosologyDTO();
        when(posologyRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<PosologyDTO> result = posologyService.updatePosology(1L, updateDTO);

        // Then
        assertFalse(result.isPresent());
        verify(posologyRepository).findById(1L);
        verify(posologyRepository, never()).save(any(Posology.class));
    }

    @Test
    void testDeletePosology_Found() {
        // Given
        when(posologyRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = posologyService.deletePosology(1L);

        // Then
        assertTrue(result);
        verify(posologyRepository).existsById(1L);
        verify(posologyRepository).deleteById(1L);
    }

    @Test
    void testDeletePosology_NotFound() {
        // Given
        when(posologyRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = posologyService.deletePosology(1L);

        // Then
        assertFalse(result);
        verify(posologyRepository).existsById(1L);
        verify(posologyRepository, never()).deleteById(1L);
    }
}