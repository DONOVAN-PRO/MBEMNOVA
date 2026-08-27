package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.DTO.CotisationDTO;
import com.MBEMNOVA.Tontine.Entity.Cotisation;
import com.MBEMNOVA.Tontine.Service.CotisationService;
import com.MBEMNOVA.Tontine.Service.MembreService;
import com.MBEMNOVA.Tontine.Service.TontineService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tontines/{tontineId}/cotisations")
public class CotisationController {

    private final CotisationService cotisationService;
    private final MembreService membreService;
    private final TontineService tontineService;

    public CotisationController(
            CotisationService cotisationService,
            MembreService membreService,
            TontineService tontineService
    ) {
        this.cotisationService = cotisationService;
        this.membreService = membreService;
        this.tontineService = tontineService;
    }

    /**
     * Liste des cotisations.
     */
    @GetMapping
    public String liste(
            @PathVariable Long tontineId,
            Model model
    ) {

        model.addAttribute(
                "tontine",
                tontineService.findById(tontineId)
        );

        model.addAttribute(
                "cotisations",
                cotisationService.findByTontine(tontineId)
        );

        return "cotisations/liste";
    }

    /**
     * Formulaire d'ajout.
     */
    @GetMapping("/nouveau")
    public String nouveau(
            @PathVariable Long tontineId,
            Model model
    ) {

        CotisationDTO dto = new CotisationDTO();

        // Ne pas remplir tontineId ici : ce champ n'est plus lié au formulaire
        // (voir CotisationDTO — il est désormais renseigné uniquement côté serveur).
        dto.setStatut(
                Cotisation.StatutCotisation.PAYE
        );

        model.addAttribute(
                "tontine",
                tontineService.findById(tontineId)
        );

        model.addAttribute(
                "cotisationDTO",
                dto
        );

        model.addAttribute(
                "membres",
                membreService.findAll()
        );

        return "cotisations/formulaire";
    }

    /**
     * Enregistrement.
     */
    @PostMapping
    public String creer(
            @PathVariable Long tontineId,
            @Valid @ModelAttribute("cotisationDTO")
            CotisationDTO dto,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "tontine",
                    tontineService.findById(tontineId)
            );

            model.addAttribute(
                    "membres",
                    membreService.findAll()
            );

            return "cotisations/formulaire";
        }

        // tontineId vient toujours de l'URL, jamais du formulaire soumis par l'utilisateur.
        dto.setTontineId(tontineId);

        cotisationService.create(dto);

        return "redirect:/tontines/"
                + tontineId
                + "/cotisations";
    }
}