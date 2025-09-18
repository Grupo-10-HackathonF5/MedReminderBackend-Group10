package com.hackathon.medreminder.medication.entity;

import com.hackathon.medreminder.posology.entity.Posology;
import com.hackathon.medreminder.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "dosage_quantity", nullable = false)
    private Integer dosageQuantity;

    @Column(name = "dosage_unit", nullable = false)
    private String dosageUnit;

    @Column(nullable = false)
    private Boolean active;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default  // This fixes the warning
    private List<Posology> posologies = new ArrayList<>();
}