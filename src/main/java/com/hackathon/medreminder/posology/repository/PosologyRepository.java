package com.hackathon.medreminder.posology.repository;

import com.hackathon.medreminder.posology.entity.Posology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PosologyRepository extends JpaRepository<Posology, Long> {
    
    // Buscar posologías por medicamento
    List<Posology> findByMedicationId(Long medicationId);
    
    // Buscar posologías activas (sin fecha de fin o fecha de fin futura)
    @Query("SELECT p FROM Posology p WHERE p.endDate IS NULL OR p.endDate >= :currentDate")
    List<Posology> findActivePosologies(@Param("currentDate") LocalDate currentDate);
    
    // Buscar posologías por medicamento que estén activas
    @Query("SELECT p FROM Posology p WHERE p.medicationId = :medicationId AND (p.endDate IS NULL OR p.endDate >= :currentDate)")
    List<Posology> findActivePosologiesByMedicationId(@Param("medicationId") Long medicationId, @Param("currentDate") LocalDate currentDate);
    
    // Buscar posologías por rango de fechas
    @Query("SELECT p FROM Posology p WHERE p.startDate <= :endDate AND (p.endDate IS NULL OR p.endDate >= :startDate)")
    List<Posology> findPosologiesInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}