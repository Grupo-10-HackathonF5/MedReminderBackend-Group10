package com.hackathon.medreminder.medication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.repository.MedicationRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class MedicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MedicationRepository medicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Medication medication;
    private User user;

    @BeforeEach
    void setup() {
        medicationRepository.deleteAll();
        userRepository.deleteAll();

        User unsavedUser = User.builder()
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        user = userRepository.save(unsavedUser);

        medication = Medication.builder()
                .user(user)
                .name("Ibuprofen")
                .dosageQuantity(1)
                .dosageUnit("1 mg")
                .active(true)
                .notes("Take with food")
                .build();

        medication = medicationRepository.save(medication);
    }

    @Test
    void testGetMedicationById() throws Exception {
        mockMvc.perform(get("/api/medications/" + medication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ibuprofen"))
                .andExpect(jsonPath("$.dosageQuantity").value(1));
    }

    @Test
    void testCreateMedication() throws Exception {
        String createJson = String.format("""
            {
              "userId": %d,
              "name": "Paracetamol",
              "dosageQuantity": 2,
              "dosageUnit": "mg",
              "active": true,
              "notes": "Take after food"
            }
            """, user.getId());

        mockMvc.perform(post("/api/medications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Paracetamol"))
                .andExpect(jsonPath("$.dosageQuantity").value(2))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdateMedication() throws Exception {
        String updateJson = String.format("""
            {
              "userId": %d,
              "name": "Ibuprofen Updated",
              "dosageQuantity": 5,
              "dosageUnit": "mg",
              "active": false,
              "notes": "Modified note"
            }
            """, user.getId());

        mockMvc.perform(put("/api/medications/" + medication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ibuprofen Updated"))
                .andExpect(jsonPath("$.dosageQuantity").value(5))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testDeleteMedication() throws Exception {
        mockMvc.perform(delete("/api/medications/" + medication.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/medications/" + medication.getId()))
                .andExpect(status().isNotFound());
    }
}

