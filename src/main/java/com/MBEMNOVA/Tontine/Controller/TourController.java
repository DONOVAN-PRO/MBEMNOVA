package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.Entity.Tour;
import com.MBEMNOVA.Tontine.Service.TourService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    /**
     * Liste des tours.
     */
    @GetMapping("/tontines/{id}/tours")
    public String liste(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "tours",
                tourService.findByTontine(id)
        );

        model.addAttribute(
                "tontineId",
                id
        );

        return "tours/liste";
    }

    /**
     * Génération du prochain tour.
     */
    @PostMapping("/tontines/{id}/tours/generer")
    public String generer(
            @PathVariable Long id
    ) {

        tourService.genererProchainTour(id);

        return "redirect:/tontines/" + id + "/tours";
    }

    /**
     * Marque le tour comme versé.
     * Redirige vers la liste des tours de la tontine concernée
     * (et non vers la liste générale des tontines).
     */
    @PostMapping("/tours/{id}/verser")
    public String verser(
            @PathVariable Long id
    ) {

        Tour tour = tourService.verser(id);

        return "redirect:/tontines/" + tour.getTontine().getId() + "/tours";
    }
}