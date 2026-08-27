package com.MBEMNOVA.Tontine.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "tours",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tontine_cycle",
                        columnNames = {"tontine_id", "numero_cycle"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    @NotNull(message = "Le numéro de cycle est obligatoire")
    @Column(name = "numero_cycle", nullable = false)
    private Integer numeroCycle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_beneficiaire_id", nullable = false)
    private Membre membreBeneficiaire;

    @NotNull(message = "La date prévue est obligatoire")
    @Column(name = "date_prevue", nullable = false)
    private LocalDate datePrevue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutTour statut = StatutTour.A_VENIR;

    public enum StatutTour {
        A_VENIR,
        VERSE
    }
}
