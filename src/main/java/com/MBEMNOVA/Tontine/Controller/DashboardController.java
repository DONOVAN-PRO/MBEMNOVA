package com.MBEMNOVA.Tontine.Controller;

import com.MBEMNOVA.Tontine.Service.CotisationService;
import com.MBEMNOVA.Tontine.Service.TontineService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final TontineService tontineService;
    private final CotisationService cotisationService;

    public DashboardController(
            TontineService tontineService,
            CotisationService cotisationService
    ) {
        this.tontineService = tontineService;
        this.cotisationService = cotisationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            @AuthenticationPrincipal OAuth2User user
    ) {

        var tontines = tontineService.findAll();

        model.addAttribute(
                "tontines",
                tontines
        );

        model.addAttribute(
                "nombreTontines",
                tontines.size()
        );

        model.addAttribute(
                "nombreTontinesActives",
                tontineService
                        .findActiveTontines()
                        .size()
        );

        if (user != null) {

            model.addAttribute(
                    "nomUtilisateur",
                    user.getAttribute("name")
            );

            model.addAttribute(
                    "emailUtilisateur",
                    user.getAttribute("email")
            );

            model.addAttribute(
                    "photoUtilisateur",
                    user.getAttribute("picture")
            );
        }

        return "dashboard";
    }
}