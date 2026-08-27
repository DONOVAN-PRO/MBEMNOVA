package com.MBEMNOVA.Tontine.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "adhesions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_membre_tontine",
                        columnNames = {"membre_id", "tontine_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tontine_id", nullable = false)
    private Tontine tontine;

    @NotNull(message = "L'ordre de tour est obligatoire")
    @Min(value = 1, message = "L'ordre doit être supérieur ou égal à 1")
    @Column(name = "ordre_tour", nullable = false)
    private Integer ordreTour;

    @NotNull(message = "La date d'adhésion est obligatoire")
    @Column(name = "date_adhesion", nullable = false)
    private LocalDate dateAdhesion;
}