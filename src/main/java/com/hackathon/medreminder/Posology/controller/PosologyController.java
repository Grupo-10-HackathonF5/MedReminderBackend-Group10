package com.hackathon.medreminder.Posology.controller;

import com.hackathon.medreminder.Posology.dto.PosologyDTO;
import com.hackathon.medreminder.Posology.service.PosologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/posologies")
@RequiredArgsConstructor
public class PosologyController {
    
    private final PosologyService posologyService;
    
    @GetMapping
    public ResponseEntity<List<PosologyDTO>> getAllPosologies() {
        List<PosologyDTO> posologies = posologyService.getAllPosologies();
        return ResponseEntity.ok(posologies);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PosologyDTO> getPosologyById(@PathVariable Long id) {
        return posologyService.getPosologyById(id)
                .map(posology -> ResponseEntity.ok(posology))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/medication/{medicationId}")
    public ResponseEntity<List<PosologyDTO>> getPosologiesByMedicationId(@PathVariable Long medicationId) {
        List<PosologyDTO> posologies = posologyService.getPosologiesByMedicationId(medicationId);
        return ResponseEntity.ok(posologies);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<PosologyDTO>> getActivePosologies() {
        List<PosologyDTO> activePosologies = posologyService.getActivePosologies();
        return ResponseEntity.ok(activePosologies);
    }
    
    @PostMapping
    public ResponseEntity<PosologyDTO> createPosology(@Valid @RequestBody PosologyDTO posologyDTO) {
        PosologyDTO createdPosology = posologyService.createPosology(posologyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosology);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PosologyDTO> updatePosology(@PathVariable Long id, @Valid @RequestBody PosologyDTO posologyDTO) {
        return posologyService.updatePosology(id, posologyDTO)
                .map(updatedPosology -> ResponseEntity.ok(updatedPosology))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosology(@PathVariable Long id) {
        boolean deleted = posologyService.deletePosology(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}