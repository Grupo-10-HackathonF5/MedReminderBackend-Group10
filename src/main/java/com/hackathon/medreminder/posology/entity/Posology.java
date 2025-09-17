package com.hackathon.medreminder.posology.entity;

import com.hackathon.medreminder.enums.FrequencyUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "posologies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posology {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "medication_id", nullable = false)
    private Long medicationId;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "day_time", nullable = false)
    private LocalDateTime dayTime;
    
    // Frecuencia: se toma cada frequencyValue frequencyUnit
    // Ejemplos: cada 8 horas (frequencyValue=8, frequencyUnit=HOUR)
    //          cada 2 días (frequencyValue=2, frequencyUnit=DAY)
    //          cada 1 mes (frequencyValue=1, frequencyUnit=MONTH)
    @Column(name = "frequency_value", nullable = false)
    private Integer frequencyValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_unit", nullable = false)
    private FrequencyUnit frequencyUnit;
    
    @Column(name = "quantity", nullable = false)
    private Double quantity;
    
    @Column(name = "reminder_message")
    private String reminderMessage;
    
    @Column(name = "doses_number", nullable = false)
    private Double dosesNumber;
}