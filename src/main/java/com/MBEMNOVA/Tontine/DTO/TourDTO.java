package com.MBEMNOVA.Tontine.DTO;

import com.MBEMNOVA.Tontine.Entity.Tour;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDTO {

    private Long id;

    @NotNull(message = "La tontine est obligatoire")
    private Long tontineId;

    @NotNull(message = "Le numéro de cycle est obligatoire")
    private Integer numeroCycle;

    @NotNull(message = "Le bénéficiaire est obligatoire")
    private Long membreBeneficiaireId;

    @NotNull(message = "La date prévue est obligatoire")
    private LocalDate datePrevue;

    @NotNull(message = "Le statut est obligatoire")
    private Tour.StatutTour statut;
}