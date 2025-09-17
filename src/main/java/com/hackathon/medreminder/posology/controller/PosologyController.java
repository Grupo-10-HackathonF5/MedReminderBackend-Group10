package com.hackathon.medreminder.posology.controller;

import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.PosologyService;
import com.hackathon.medreminder.shared.dto.ApiMessage;
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
    @ResponseStatus(HttpStatus.OK)
    public List<PosologyResponse> getAllPosologies() {
        List<PosologyResponse> posologies = posologyService.getAllPosologies();
        return posologies;
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PosologyResponse getPosologyById(@PathVariable Long id) {
        return posologyService.getPosologyById(id);
    }
    
    @GetMapping("/medication/{medicationId}")
    public ResponseEntity<List<PosologyResponse>> getPosologiesByMedicationId(@PathVariable Long medicationId) {
        List<PosologyResponse> posologies = posologyService.getPosologiesByMedicationId(medicationId);
        return ResponseEntity.ok(posologies);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<PosologyResponse>> getActivePosologies() {
        List<PosologyResponse> activePosologies = posologyService.getActivePosologies();
        return ResponseEntity.ok(activePosologies);
    }
    
    @PostMapping
    public ResponseEntity<PosologyResponse> createPosology(@Valid @RequestBody PosologyRequest posologyRequest) {
        PosologyResponse createdPosology = posologyService.createPosology(posologyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosology);
    }
    
    @PutMapping("/{id}")
    public PosologyResponse updatePosology(@PathVariable Long id, @Valid @RequestBody PosologyRequest posologyRequest) {
        return posologyService.updatePosology(id, posologyRequest);
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiMessage deletePosology(@PathVariable Long id) {
        String message = posologyService.deletePosology(id);
        return new ApiMessage(message);
    }
}