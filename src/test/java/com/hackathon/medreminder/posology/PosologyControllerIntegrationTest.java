package com.hackathon.medreminder.posology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

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
    private ObjectMapper objectMapper;

    private Posology posology;

    @BeforeEach
    void setup() {
        posologyRepository.deleteAll();

        posology = Posology.builder()
                .medicationId(100L)
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
                .andExpect(jsonPath("$[0].medicationId").value(100));
    }

    @Test
    void testGetPosologyById() throws Exception {
        mockMvc.perform(get("/api/posologies/{id}", posology.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(100));
    }

    @Test
    void testGetPosologyById_NotFound() throws Exception {
        mockMvc.perform(get("/api/posologies/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPosologiesByMedicationId() throws Exception {
        mockMvc.perform(get("/api/posologies/medication/{medicationId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationId").value(100));
    }

    @Test
    void testGetActivePosologies() throws Exception {
        mockMvc.perform(get("/api/posologies/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].medicationId").value(100));
    }

    @Test
    void testCreateUpdateDeletePosology() throws Exception {

        PosologyRequest request = new PosologyRequest(
                200L,
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

        String createdJson = mockMvc.perform(post("/api/posologies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.medicationId").value(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdId = objectMapper.readTree(createdJson).get("id").asLong();

        PosologyRequest updateRequest = new PosologyRequest(
                201L,
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

        mockMvc.perform(put("/api/posologies/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medicationId").value(201))
                .andExpect(jsonPath("$.reminderMessage").value("Take three times daily"));

        mockMvc.perform(delete("/api/posologies/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Posology from 201 deleted correctly"));

        mockMvc.perform(get("/api/posologies/{id}", createdId))
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



