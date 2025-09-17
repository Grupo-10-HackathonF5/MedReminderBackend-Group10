package com.hackathon.medreminder.medication.service;

import com.hackathon.medreminder.medication.dto.MedicationMapper;
import com.hackathon.medreminder.medication.dto.MedicationRequest;
import com.hackathon.medreminder.medication.dto.MedicationResponse;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.exception.MedicationNotFoundById;
import com.hackathon.medreminder.medication.repository.MedicationRepository;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;
    private final EntityMapperUtil entityMapperUtil;

    public List<MedicationResponse> getAllMedications() {
        return entityMapperUtil.mapEntitiesToDTOs(medicationRepository.findAll(), medicationMapper::toResponse);
    }

    public MedicationResponse getMedicationById(Long id) {
        return medicationMapper.toResponse(getMedicationEntityById(id));
    }

    public Medication getMedicationEntityById(Long id) {
        return medicationRepository.findById(id)
                .orElseThrow(() -> new MedicationNotFoundById(id));
    }

    public MedicationResponse createMedication(MedicationRequest medicationRequest) {
        Medication medication = medicationMapper.toMedication(medicationRequest);
        return medicationMapper.toResponse(medicationRepository.save(medication));
    }

    public MedicationResponse updateMedication(Long id, MedicationRequest medicationRequest) {
        Medication medication = getMedicationEntityById(id);
        return medicationMapper.toResponse(updateEntityFromDTO(medication, medicationRequest));
    }

    public String deleteMedication(Long id) {
        Medication medication = getMedicationEntityById(id);
        medicationRepository.deleteById(medication.getId());
        return String.format("Medication %s deleted successfully", medication.getName());
    }

    private Medication updateEntityFromDTO(Medication medication, MedicationRequest dto) {
        medication.setName(dto.name());
        medication.setDosageQuantity(dto.dosageQuantity());
        medication.setDosageUnit(dto.dosageUnit());
        medication.setActive(dto.active());
        medication.setNotes(dto.notes());

        return medicationRepository.save(medication);
    }
}
