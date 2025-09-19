package com.hackathon.medreminder.dose.repository;

import com.hackathon.medreminder.dose.entity.Dose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoseRepository extends JpaRepository<Dose, Long> {
    List<Dose> findByUser_IdAndScheduledDateTimeBetweenOrderByScheduledDateTimeAsc(Long userId, LocalDateTime from, LocalDateTime to);
    List<Dose> findByUser_Id(Long userId);
    Optional<Dose> findByPosology_IdAndScheduledDateTime(Long posologyId, LocalDateTime scheduledDateTime);
}