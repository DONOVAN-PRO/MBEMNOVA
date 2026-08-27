package com.MBEMNOVA.Tontine.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdhesionDTO {

    private Long id;

    @NotNull(message = "Le membre est obligatoire")
    private Long membreId;

    // Pas de @NotNull ici : ce champ est toujours renseigné par le contrôleur
    // depuis l'URL (/tontines/{id}/membres), jamais par le formulaire soumis
    // par l'utilisateur. Le valider ici ferait échouer la validation à chaque
    // soumission, puisqu'il est encore null au moment du data binding.
    private Long tontineId;

    @NotNull(message = "L'ordre de tour est obligatoire")
    @Min(
            value = 1,
            message = "L'ordre de tour doit être supérieur ou égal à 1"
    )
    private Integer ordreTour;

    @NotNull(message = "La date d'adhésion est obligatoire")
    private LocalDate dateAdhesion;
}