package com.hackathon.medreminder.medication;

import com.hackathon.medreminder.medication.dto.MedicationMapper;
import com.hackathon.medreminder.medication.dto.MedicationRequest;
import com.hackathon.medreminder.medication.dto.MedicationResponse;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.exception.MedicationNotFoundById;
import com.hackathon.medreminder.medication.repository.MedicationRepository;
import com.hackathon.medreminder.medication.service.MedicationService;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private MedicationMapper medicationMapper;

    @Mock
    private EntityMapperUtil entityMapperUtil;

    @InjectMocks
    private MedicationService medicationService;

    private Medication medication;
    private MedicationRequest medicationRequest;
    private MedicationResponse medicationResponse;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(42L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(null) // Or set a valid role
                .build();

        medication = Medication.builder()
                .id(1L)
                .user(user)
                .name("Ibuprofen")
                .dosageQuantity(1)
                .dosageUnit("1 mg")
                .active(true)
                .notes("Take with food")
                .build();

        medicationRequest = new MedicationRequest(
                42L,                // User ID
                "Ibuprofen",
                1,
                "1 mg",
                true,
                "Take with food"
        );

        medicationResponse = new MedicationResponse(
                1L,
                42L,
                "Ibuprofen",
                1,
                "1 mg",
                true,
                "Take with food"
        );
    }

    @Test
    void getAllMedications_returnsMappedList() {
        List<Medication> entities = List.of(medication);
        List<MedicationResponse> dtos = List.of(medicationResponse);

        when(medicationRepository.findAll()).thenReturn(entities);
        when(entityMapperUtil.mapEntitiesToDTOs(eq(entities), any())).thenReturn(List.of(medicationResponse));

        List<MedicationResponse> result = medicationService.getAllMedications();

        assertEquals(dtos, result);
        verify(medicationRepository).findAll();
        verify(entityMapperUtil).mapEntitiesToDTOs(eq(entities), any());
    }

    @Test
    void getMedicationById_found() {
        when(medicationRepository.findById(medication.getId())).thenReturn(Optional.of(medication));
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse response = medicationService.getMedicationById(medication.getId());

        assertEquals(medicationResponse, response);
        verify(medicationRepository).findById(medication.getId());
        verify(medicationMapper).toResponse(medication);
    }

    @Test
    void getMedicationById_notFound_throws() {
        when(medicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MedicationNotFoundById.class, () -> medicationService.getMedicationById(999L));
    }

    @Test
    void createMedication_savesAndMaps() {
        when(userService.getUserEntityById(medicationRequest.userId())).thenReturn(user);
        when(medicationMapper.toMedication(medicationRequest)).thenReturn(medication);
        when(medicationRepository.save(medication)).thenReturn(medication);
        when(medicationMapper.toResponse(medication)).thenReturn(medicationResponse);

        MedicationResponse response = medicationService.createMedication(medicationRequest);

        assertEquals(medicationResponse, response);
        verify(userService).getUserEntityById(medicationRequest.userId());
        verify(medicationMapper).toMedication(medicationRequest);
        verify(medicationRepository).save(medication);
        verify(medicationMapper).toResponse(medication);
    }

    @Test
    void updateMedication_updatesAndMaps() {
        Medication updatedMedication = Medication.builder()
                .id(medication.getId())
                .user(user)
                .name(medicationRequest.name())
                .dosageQuantity(medicationRequest.dosageQuantity())
                .dosageUnit(medicationRequest.dosageUnit())
                .active(medicationRequest.active())
                .notes(medicationRequest.notes())
                .build();

        when(medicationRepository.findById(medication.getId())).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(Medication.class))).thenReturn(updatedMedication);
        when(medicationMapper.toResponse(any(Medication.class))).thenReturn(medicationResponse);

        MedicationResponse response = medicationService.updateMedication(medication.getId(), medicationRequest);

        assertEquals(medicationResponse, response);
        verify(medicationRepository).findById(medication.getId());
        verify(medicationRepository).save(any(Medication.class));
        verify(medicationMapper).toResponse(any(Medication.class));
    }

    @Test
    void deleteMedication_deletesAndReturnsMessage() {
        when(medicationRepository.findById(medication.getId())).thenReturn(Optional.of(medication));
        doNothing().when(medicationRepository).deleteById(medication.getId());

        String message = medicationService.deleteMedication(medication.getId());

        assertEquals(String.format("Medication %s deleted successfully", medication.getName()), message);
        verify(medicationRepository).findById(medication.getId());
        verify(medicationRepository).deleteById(medication.getId());
    }
}
