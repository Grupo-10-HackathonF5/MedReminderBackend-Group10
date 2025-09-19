package com.hackathon.medreminder.posology.service;

import com.hackathon.medreminder.dose.service.DoseSchedulerService;
import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.medication.service.MedicationService;
import com.hackathon.medreminder.posology.dto.PosologyMapper;
import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.posology.exception.PosologyNotFoundById;
import com.hackathon.medreminder.posology.repository.PosologyRepository;
import com.hackathon.medreminder.shared.util.EntityMapperUtil;
import com.hackathon.medreminder.user.entity.User;
import com.hackathon.medreminder.user.service.UserService;
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
    private final MedicationService medicationService;
    private final UserService userService;
    private final DoseSchedulerService doseSchedulerService;
    
    public List<PosologyResponse> getAllPosologies() {
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findAll(), posologyMapper::toResponse);
    }

    public PosologyResponse getPosologyById(Long id) {
        return posologyMapper.toResponse(getPosologyEntityById(id));
    }


    public List<PosologyResponse> getActivePosologiesByUserId(Long userId) {
        User user = userService.getUserEntityById(userId);
        List<Posology> list = posologyRepository.findByUser_IdAndEndDateIsNullOrEndDateAfter(userId, LocalDate.now());
        return entityMapperUtil.mapEntitiesToDTOs(list, posologyMapper::toResponse);
    }

    public List<PosologyResponse> getPosologiesByUserId(Long userId) {
        User user = userService.getUserEntityById(userId);
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findByUser_Id(userId), posologyMapper::toResponse);
    }
    
    public Posology getPosologyEntityById(Long id) {
        return posologyRepository.findById(id).
                orElseThrow(() -> new PosologyNotFoundById(id));
    }
    
    public List<PosologyResponse> getPosologiesByMedicationId(Long medicationId) {
        return entityMapperUtil.mapEntitiesToDTOs(posologyRepository.findByMedicationId(medicationId), posologyMapper::toResponse);
    }
    
    public PosologyResponse createPosology(PosologyRequest posologyRequest) {
        Medication medication = medicationService.getMedicationEntityById(posologyRequest.medicationId());
        User user = userService.getUserEntityById(posologyRequest.userId());
        Posology posology = posologyMapper.toPosology(posologyRequest);
        posology.setUser(user);
        posology.setMedication(medication);
        Posology savedPosology = posologyRepository.save(posology);
        doseSchedulerService.scheduleDosesForPosology(savedPosology);
        return posologyMapper.toResponse(savedPosology);
    }
    
    public PosologyResponse updatePosology(Long id, PosologyRequest posologyRequest) {
        Posology posology = getPosologyEntityById(id);
        return posologyMapper.toResponse(updateEntityFromDTO(posology, posologyRequest));
    }
    
    public String deletePosology(Long id) {
        Posology posology = getPosologyEntityById(id);
        posologyRepository.deleteById(posology.getId());
        return String.format("Posology from %s deleted correctly", posology.getMedication().getName());
    }
    
    private Posology updateEntityFromDTO(Posology posology, PosologyRequest dto) {
        //check medication id exists
        Medication medication = medicationService.getMedicationEntityById(dto.medicationId());
        posology.setMedication(medication);
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