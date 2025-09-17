package com.hackathon.medreminder.posology;

import com.hackathon.medreminder.posology.dto.PosologyMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PosologyService {
    
    private final PosologyRepository posologyRepository;
    private final PosologyMapper posologyMapper;
    private final EntityMapperUtil entityMapperUtil;
    
    public List<PosologyResponse> getAllPosologies() {
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findAll(), posologyMapper::toResponse);
    }

    public PosologyResponse getPosologyById(Long id) {
        return posologyMapper.toResponse(getPosologyEntityById(id));
    }
    
    public Posology getPosologyEntityById(Long id) {
        return posologyRepository.findById(id).
                orElseThrow(() -> new PosologyNotFoundById(id));
    }
    
    public List<PosologyResponse> getPosologiesByMedicationId(Long medicationId) {
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findByMedicationId(medicationId), posologyMapper::toResponse);
    }
    
    public List<PosologyResponse> getActivePosologies() {
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findActivePosologies(LocalDate.now()), posologyMapper::toResponse);

    }
    
    public PosologyResponse createPosology(PosologyRequest posologyRequest) {
        Posology posology = posologyMapper.toPosology(posologyRequest);
        return posologyMapper.toResponse(posologyRepository.save(posology));
    }
    
    public PosologyResponse updatePosology(Long id, PosologyRequest posologyRequest) {
        Posology posology = getPosologyEntityById(id);
        return posologyMapper.toResponse(updateEntityFromDTO(posology, posologyRequest));
    }
    
    public String deletePosology(Long id) {
        Posology posology = getPosologyEntityById(id);
        posologyRepository.deleteById(posology.getId());
        return String.format("Posology from %s deleted correctly", posology.getMedicationId());
    }
    
    private Posology updateEntityFromDTO(Posology posology, PosologyRequest dto) {
        //check medication id exists
        posology.setMedicationId(dto.medicationId());
        posology.setStartDate(dto.startDate());
        posology.setEndDate(dto.endDate());
        posology.setDayTime(dto.dayTime());
        posology.setFrequencyValue(dto.frequencyValue());
        posology.setFrequencyUnit(dto.frequencyUnit());
        posology.setQuantity(dto.quantity());
        posology.setReminderMessage(dto.reminderMessage());
        posology.setDosesNumber(dto.dosesNumber());

        return posologyRepository.save(posology);

    }
}