package com.hackathon.medreminder.posology.controller;

import com.hackathon.medreminder.posology.dto.PosologyRequest;
import com.hackathon.medreminder.posology.dto.PosologyResponse;
import com.hackathon.medreminder.posology.service.PosologyService;
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

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PosologyResponse> getPosologiesByUserId(Long userId) {
        List<PosologyResponse> posologies = posologyService.getPosologiesByUserId(userId);
        return posologies;
    }

    @GetMapping("/users/{userId}/active")
    @ResponseStatus(HttpStatus.OK)
    public List<PosologyResponse> getActivePosologiesByUserId(Long userId) {
        List<PosologyResponse> posologies = posologyService.getActivePosologiesByUserId(userId);
        return posologies;
    }
    
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PosologyResponse getPosologyById(@PathVariable Long id) {
        return posologyService.getPosologyById(id);
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