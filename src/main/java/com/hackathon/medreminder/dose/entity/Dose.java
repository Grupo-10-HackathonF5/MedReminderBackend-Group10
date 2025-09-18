package com.hackathon.medreminder.dose.entity;

import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "doses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduled_day")
    private LocalDate scheduledDay;

    @Column(name = "scheduled_date_time")
    private LocalDateTime scheduledDateTime;

    @Column(name = "isTaken")
    private Boolean isTaken;

    @Column(name = "taken_time")
    private LocalDateTime takenTime;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "posology_id", nullable = false)
    private Posology posology;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
