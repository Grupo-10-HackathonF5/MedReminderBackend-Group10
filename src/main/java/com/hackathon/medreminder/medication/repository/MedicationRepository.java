package com.hackathon.medreminder.medication.repository;

import com.hackathon.medreminder.medication.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByUser_Id(Long userId);
}