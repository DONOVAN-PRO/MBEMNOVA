package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.Service.StatistiqueService;
import com.MBEMNOVA.Tontine.Service.TontineService;
import com.MBEMNOVA.Tontine.Service.TourService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tontines")
public class StatistiqueController {

    private final TontineService tontineService;
    private final StatistiqueService statistiqueService;
    private final TourService tourService;

    public StatistiqueController(
            TontineService tontineService,
            StatistiqueService statistiqueService,
            TourService tourService
    ) {
        this.tontineService = tontineService;
        this.statistiqueService = statistiqueService;
        this.tourService = tourService;
    }

    @GetMapping("/{id}/statistiques")
    public String statistiques(
            @PathVariable Long id,
            Model model
    ) {

        model.addAttribute(
                "tontine",
                tontineService.findById(id)
        );

        model.addAttribute(
                "totalCollecte",
                statistiqueService.totalCollecte(id)
        );

        model.addAttribute(
                "nombreCotisations",
                statistiqueService.nombreCotisations(id)
        );

        model.addAttribute(
                "nombreRetards",
                statistiqueService.nombreRetards(id)
        );

        model.addAttribute(
                "tauxRetard",
                statistiqueService.tauxRetard(id)
        );

        model.addAttribute(
                "statistiquesMembres",
                statistiqueService
                        .statistiquesMembres(id)
        );

        model.addAttribute(
                "tours",
                tourService.findByTontine(id)
        );

        return "tontines/statistiques";
    }
}