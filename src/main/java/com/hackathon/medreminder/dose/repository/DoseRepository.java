package com.hackathon.medreminder.dose.repository;

import com.hackathon.medreminder.dose.entity.Dose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoseRepository extends JpaRepository<Dose, Long> {

    List<Dose> findByUser_IdAndScheduledDateTimeBetween(Long userId, LocalDateTime from, LocalDateTime to);
}