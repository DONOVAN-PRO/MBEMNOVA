package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.DTO.MembreDTO;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Service.MembreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService) {
        this.membreService = membreService;
    }

    /**
     * Liste des membres.
     */
    @GetMapping
    public String liste(Model model) {

        model.addAttribute(
                "membres",
                membreService.findAll()
        );

        return "membres/liste";
    }

    /**
     * Formulaire de création.
     */
    @GetMapping("/nouveau")
    public String nouveau(Model model) {

        MembreDTO membreDTO = new MembreDTO();

        membreDTO.setStatut(
                Membre.StatutMembre.ACTIF
        );

        model.addAttribute("membreDTO", membreDTO);

        return "membres/formulaire";
    }

    /**
     * Enregistrement d'un nouveau membre.
     */
    @PostMapping
    public String creer(
            @Valid @ModelAttribute("membreDTO") MembreDTO dto,
            BindingResult result
    ) {

        if (result.hasErrors()) {
            return "membres/formulaire";
        }

        try {

            membreService.create(dto);

        } catch (RuntimeException e) {

            result.rejectValue(
                    "email",
                    "email.existe",
                    e.getMessage()
            );

            return "membres/formulaire";
        }

        return "redirect:/membres";
    }

    /**
     * Formulaire de modification.
     */
    @GetMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            Model model
    ) {

        Membre membre = membreService.findById(id);

        MembreDTO dto = convertirVersDTO(membre);

        model.addAttribute(
                "membreDTO",
                dto
        );

        return "membres/formulaire";
    }

    /**
     * Mise à jour d'un membre.
     */
    @PostMapping("/{id}/modifier")
    public String mettreAJour(
            @PathVariable Long id,
            @Valid @ModelAttribute("membreDTO") MembreDTO dto,
            BindingResult result
    ) {

        if (result.hasErrors()) {
            return "membres/formulaire";
        }

        try {

            membreService.update(id, dto);

        } catch (RuntimeException e) {

            result.rejectValue(
                    "email",
                    "email.erreur",
                    e.getMessage()
            );

            return "membres/formulaire";
        }

        return "redirect:/membres";
    }

    /**
     * Suppression d'un membre.
     */
    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id
    ) {

        membreService.delete(id);

        return "redirect:/membres";
    }

    /**
     * Conversion Entity → DTO.
     */
    private MembreDTO convertirVersDTO(Membre membre) {

        return MembreDTO.builder()
                .id(membre.getId())
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .telephone(membre.getTelephone())
                .dateAdhesion(membre.getDateAdhesion())
                .statut(membre.getStatut())
                .build();
    }
}