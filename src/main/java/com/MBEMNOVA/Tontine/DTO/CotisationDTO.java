package com.MBEMNOVA.Tontine.DTO;

import com.MBEMNOVA.Tontine.Entity.Cotisation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotisationDTO {

    private Long id;

    @NotNull(message = "Le membre est obligatoire")
    private Long membreId;

    // Pas de @NotNull ici : ce champ est toujours renseigné par le contrôleur
    // depuis l'URL (/tontines/{tontineId}/cotisations), jamais par le formulaire
    // soumis par l'utilisateur. Le valider ici ferait échouer la validation
    // à chaque soumission, puisqu'il est encore null au moment du data binding.
    private Long tontineId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(
            value = "0.01",
            message = "Le montant doit être supérieur à 0"
    )
    private BigDecimal montant;

    // Optionnelle : une cotisation "en retard" n'a pas encore de date de paiement.
    private LocalDate datePaiement;

    @NotNull(message = "Le cycle est obligatoire")
    private Integer cycle;

    @NotNull(message = "Le statut est obligatoire")
    private Cotisation.StatutCotisation statut;
}