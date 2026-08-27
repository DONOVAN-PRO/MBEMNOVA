package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.DTO.AdhesionDTO;
import com.MBEMNOVA.Tontine.DTO.TontineDTO;
import com.MBEMNOVA.Tontine.Entity.Tontine;
import com.MBEMNOVA.Tontine.Service.AdhesionService;
import com.MBEMNOVA.Tontine.Service.CotisationService;
import com.MBEMNOVA.Tontine.Service.MembreService;
import com.MBEMNOVA.Tontine.Service.TontineService;
import com.MBEMNOVA.Tontine.Service.TourService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tontines")
public class TontineController {

    private final TontineService tontineService;
    private final AdhesionService adhesionService;
    private final CotisationService cotisationService;
    private final TourService tourService;
    private final MembreService membreService;

    public TontineController(
            TontineService tontineService,
            AdhesionService adhesionService,
            CotisationService cotisationService,
            TourService tourService,
            MembreService membreService
    ) {
        this.tontineService = tontineService;
        this.adhesionService = adhesionService;
        this.cotisationService = cotisationService;
        this.tourService = tourService;
        this.membreService = membreService;
    }

    /**
     * Liste des tontines.
     */
    @GetMapping
    public String liste(Model model) {

        model.addAttribute(
                "tontines",
                tontineService.findAll()
        );

        return "tontines/liste";
    }

    /**
     * Formulaire de création.
     */
    @GetMapping("/nouveau")
    public String nouveau(Model model) {

        TontineDTO dto = new TontineDTO();

        dto.setStatut(
                Tontine.StatutTontine.ACTIVE
        );

        model.addAttribute(
                "tontineDTO",
                dto
        );

        model.addAttribute(
                "frequences",
                Tontine.Frequence.values()
        );

        model.addAttribute(
                "statuts",
                Tontine.StatutTontine.values()
        );

        return "tontines/formulaire";
    }

    /**
     * Création d'une tontine.
     */
    @PostMapping
    public String creer(
            @Valid @ModelAttribute("tontineDTO") TontineDTO dto,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "frequences",
                    Tontine.Frequence.values()
            );

            model.addAttribute(
                    "statuts",
                    Tontine.StatutTontine.values()
            );

            return "tontines/formulaire";
        }

        tontineService.create(dto);

        return "redirect:/tontines";
    }

    /**
     * Détail d'une tontine.
     */
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        Tontine tontine = tontineService.findById(id);

        model.addAttribute(
                "tontine",
                tontine
        );

        model.addAttribute(
                "adhesions",
                adhesionService.findByTontine(id)
        );

        model.addAttribute(
                "cotisations",
                cotisationService.findByTontine(id)
        );

        model.addAttribute(
                "tours",
                tourService.findByTontine(id)
        );

        model.addAttribute(
                "totalCollecte",
                cotisationService.getTotalCollecte(id)
        );

        model.addAttribute(
                "tauxRetard",
                cotisationService.getTauxRetard(id)
        );

        // Nécessaires pour le formulaire d'inscription d'un membre à cette tontine
        model.addAttribute(
                "membresDisponibles",
                membreService.findAll()
        );

        model.addAttribute(
                "adhesionDTO",
                AdhesionDTO.builder().dateAdhesion(java.time.LocalDate.now()).build()
        );

        return "tontines/detail";
    }

    /**
     * Formulaire de modification.
     */
    @GetMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            Model model
    ) {

        Tontine tontine =
                tontineService.findById(id);

        TontineDTO dto = convertirVersDTO(tontine);

        model.addAttribute(
                "tontineDTO",
                dto
        );

        model.addAttribute(
                "frequences",
                Tontine.Frequence.values()
        );

        model.addAttribute(
                "statuts",
                Tontine.StatutTontine.values()
        );

        return "tontines/formulaire";
    }

    /**
     * Mise à jour.
     */
    @PostMapping("/{id}/modifier")
    public String mettreAJour(
            @PathVariable Long id,
            @Valid @ModelAttribute("tontineDTO") TontineDTO dto,
            BindingResult result,
            Model model
    ) {

        if (result.hasErrors()) {

            model.addAttribute(
                    "frequences",
                    Tontine.Frequence.values()
            );

            model.addAttribute(
                    "statuts",
                    Tontine.StatutTontine.values()
            );

            return "tontines/formulaire";
        }

        tontineService.update(id, dto);

        return "redirect:/tontines/" + id;
    }

    /**
     * Suppression.
     */
    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id
    ) {

        tontineService.delete(id);

        return "redirect:/tontines";
    }

    /**
     * Entity → DTO.
     */
    private TontineDTO convertirVersDTO(
            Tontine tontine
    ) {

        return TontineDTO.builder()
                .id(tontine.getId())
                .nom(tontine.getNom())
                .montantCotisation(
                        tontine.getMontantCotisation()
                )
                .frequence(tontine.getFrequence())
                .dateDebut(tontine.getDateDebut())
                .statut(tontine.getStatut())
                .build();
    }
}