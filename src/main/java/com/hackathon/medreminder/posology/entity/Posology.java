package com.hackathon.medreminder.posology.entity;

import com.hackathon.medreminder.medication.entity.Medication;
import com.hackathon.medreminder.posology.frecuency.FrequencyUnit;
import com.hackathon.medreminder.user.entity.User;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "posologies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posology {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;
    
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}