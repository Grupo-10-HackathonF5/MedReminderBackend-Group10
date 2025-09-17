package com.hackathon.medreminder.Posology.service;

import com.hackathon.medreminder.Posology.dto.PosologyDTO;
import com.hackathon.medreminder.Posology.entity.Posology;
import com.hackathon.medreminder.Posology.repository.PosologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PosologyService {
    
    private final PosologyRepository posologyRepository;
    
    public List<PosologyDTO> getAllPosologies() {
        return posologyRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<PosologyDTO> getPosologyById(Long id) {
        return posologyRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    public List<PosologyDTO> getPosologiesByMedicationId(Long medicationId) {
        return posologyRepository.findByMedicationId(medicationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PosologyDTO> getActivePosologies() {
        return posologyRepository.findActivePosologies(LocalDate.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PosologyDTO createPosology(PosologyDTO posologyDTO) {
        Posology posology = convertToEntity(posologyDTO);
        posology.setId(null); // Asegurar que es una nueva entidad
        Posology savedPosology = posologyRepository.save(posology);
        return convertToDTO(savedPosology);
    }
    
    public Optional<PosologyDTO> updatePosology(Long id, PosologyDTO posologyDTO) {
        return posologyRepository.findById(id)
                .map(existingPosology -> {
                    updateEntityFromDTO(existingPosology, posologyDTO);
                    existingPosology.setId(id); // Mantener el ID original
                    Posology updatedPosology = posologyRepository.save(existingPosology);
                    return convertToDTO(updatedPosology);
                });
    }
    
    public boolean deletePosology(Long id) {
        if (posologyRepository.existsById(id)) {
            posologyRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Métodos de conversión
    private PosologyDTO convertToDTO(Posology posology) {
        return new PosologyDTO(
                posology.getId(),
                posology.getMedicationId(),
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
    
    private Posology convertToEntity(PosologyDTO dto) {
        return new Posology(
                dto.getId(),
                dto.getMedicationId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getDayTime(),
                dto.getFrequencyValue(),
                dto.getFrequencyUnit(),
                dto.getQuantity(),
                dto.getReminderMessage(),
                dto.getDosesNumber()
        );
    }
    
    private void updateEntityFromDTO(Posology posology, PosologyDTO dto) {
        posology.setMedicationId(dto.getMedicationId());
        posology.setStartDate(dto.getStartDate());
        posology.setEndDate(dto.getEndDate());
        posology.setDayTime(dto.getDayTime());
        posology.setFrequencyValue(dto.getFrequencyValue());
        posology.setFrequencyUnit(dto.getFrequencyUnit());
        posology.setQuantity(dto.getQuantity());
        posology.setReminderMessage(dto.getReminderMessage());
        posology.setDosesNumber(dto.getDosesNumber());
    }
}