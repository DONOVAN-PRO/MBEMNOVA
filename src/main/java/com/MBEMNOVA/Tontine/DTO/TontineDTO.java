package com.MBEMNOVA.Tontine.DTO;

import com.MBEMNOVA.Tontine.Entity.Tontine;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TontineDTO {

    private Long id;

    @NotBlank(message = "Le nom de la tontine est obligatoire")
    private String nom;

    @NotNull(message = "Le montant de cotisation est obligatoire")
    @DecimalMin(
            value = "0.01",
            message = "Le montant doit être supérieur à 0"
    )
    private BigDecimal montantCotisation;

    @NotNull(message = "La fréquence est obligatoire")
    private Tontine.Frequence frequence;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "Le statut est obligatoire")
    private Tontine.StatutTontine statut;
}