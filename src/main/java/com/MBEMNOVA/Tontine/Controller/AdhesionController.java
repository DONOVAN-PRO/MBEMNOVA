package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.DTO.AdhesionDTO;
import com.MBEMNOVA.Tontine.Service.AdhesionService;
import com.MBEMNOVA.Tontine.Service.MembreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdhesionController {

    private final AdhesionService adhesionService;
    private final MembreService membreService;

    public AdhesionController(
            AdhesionService adhesionService,
            MembreService membreService
    ) {
        this.adhesionService = adhesionService;
        this.membreService = membreService;
    }

    /**
     * Ajouter un membre à une tontine.
     */
    @PostMapping("/tontines/{id}/membres")
    public String ajouterMembre(
            @PathVariable Long id,
            @Valid @ModelAttribute("adhesionDTO") AdhesionDTO dto,
            BindingResult result
    ) {

        if (result.hasErrors()) {
            return "redirect:/tontines/" + id;
        }

        dto.setTontineId(id);

        adhesionService.create(dto);

        return "redirect:/tontines/" + id;
    }

    /**
     * Retirer un membre d'une tontine.
     */
    @PostMapping(
            "/tontines/{id}/membres/{membreId}/retirer"
    )
    public String retirerMembre(
            @PathVariable Long id,
            @PathVariable Long membreId
    ) {

        adhesionService.removeMemberFromTontine(
                id,
                membreId
        );

        return "redirect:/tontines/" + id;
    }
}
