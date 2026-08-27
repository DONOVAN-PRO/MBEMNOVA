package com.MBEMNOVA.Tontine.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotisations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotisation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    // La date de paiement n'est pas obligatoire : une cotisation "en retard"
    // n'a, par définition, pas encore été payée. La rendre obligatoire empêchait
    // d'enregistrer une cotisation en retard, ce que le sujet demande explicitement.
    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @NotNull(message = "Le cycle est obligatoire")
    @Column(nullable = false)
    private Integer cycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutCotisation statut = StatutCotisation.PAYE;

    public enum StatutCotisation {
        PAYE,
        EN_RETARD
    }
}