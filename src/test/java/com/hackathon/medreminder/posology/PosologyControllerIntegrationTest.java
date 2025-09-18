package com.hackathon.medreminder.posology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.repository.MedicationRepository;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.repository.UserRepository;
import com.hackathon.medreminder.user.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class PosologyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PosologyRepository posologyRepository;
    @Autowired
    private MedicationRepository medicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Posology posology;
    private Medication savedMedication;

    @BeforeEach
    void setup() {
        posologyRepository.deleteAll();
        medicationRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        Medication medication = Medication.builder()
                .user(savedUser)
                .name("Ibuprofen")
                .dosageQuantity(1)
                .dosageUnit("1 mg")
                .active(true)
                .notes("Take with food")
                .build();

        savedMedication = medicationRepository.save(medication);

        System.out.println("id" + savedMedication.getId());

        posology = Posology.builder()
                .medication(savedMedication)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(10))
                .dayTime(LocalDateTime.now())
                .frequencyValue(8)
                .frequencyUnit(FrequencyUnit.HOUR)
                .quantity(1.0)
                .reminderMessage("Take after meal")
                .dosesNumber(5.0)
                .build();

        posology = posologyRepository.save(posology);
    }

    @Test
    void testGetAllPosologies() throws Exception {
        mockMvc.perform(get("/api/posologies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$[0].medicationName").value(savedMedication.getName()));
    }

    @Test
    void testGetPosologyById() throws Exception {
        mockMvc.perform(get("/api/posologies/{id}", posology.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$.medicationName").value(savedMedication.getName()));
    }

    @Test
    void testGetPosologiesByMedicationId() throws Exception {
        mockMvc.perform(get("/api/posologies/medication/{medicationId}", savedMedication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$[0].medicationName").value(savedMedication.getName()));
    }

    @Test
    void testGetActivePosologies() throws Exception {
        mockMvc.perform(get("/api/posologies/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$[0].medicationName").value(savedMedication.getName()));
    }

    @Test
    void testCreatePosology() throws Exception {

        System.out.println("id" + savedMedication.getId());

        PosologyRequest request = new PosologyRequest(
                savedMedication.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDateTime.now(),
                12,
                FrequencyUnit.HOUR,
                2.0,
                "Take twice daily",
                3.0
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/posologies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$.reminderMessage").value("Take twice daily"));
    }

    @Test
    void testUpdatePosology() throws Exception {
        PosologyRequest updateRequest = new PosologyRequest(
                savedMedication.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                LocalDateTime.now(),
                6,
                FrequencyUnit.HOUR,
                3.0,
                "Take three times daily",
                4.0
        );

        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/posologies/{id}", posology.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(savedMedication.getId()))
                .andExpect(jsonPath("$.reminderMessage").value("Take three times daily"));
    }

    @Test
    void testDeletePosology() throws Exception {
        mockMvc.perform(delete("/api/posologies/{id}", posology.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Posology from " + savedMedication.getName() + " deleted correctly"));

        mockMvc.perform(get("/api/posologies/{id}", posology.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePosologyValidationError() throws Exception {
        PosologyRequest invalidRequest = new PosologyRequest(null, null, null, null, 0, null, 0.0, null, 0.0);

        String json = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/api/posologies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
