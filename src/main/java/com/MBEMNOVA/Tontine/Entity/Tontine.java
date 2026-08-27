package com.MBEMNOVA.Tontine.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tontines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tontine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la tontine est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotNull(message = "Le montant de cotisation est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(name = "montant_cotisation", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantCotisation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequence frequence;

    @NotNull(message = "La date de début est obligatoire")
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutTontine statut = StatutTontine.ACTIVE;

    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Adhesion> adhesions = new ArrayList<>();

    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cotisation> cotisations = new ArrayList<>();

    @OneToMany(mappedBy = "tontine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tour> tours = new ArrayList<>();

    public enum Frequence {
        HEBDOMADAIRE,
        MENSUELLE
    }

    public enum StatutTontine {
        ACTIVE,
        TERMINEE,
        SUSPENDUE
    }
}