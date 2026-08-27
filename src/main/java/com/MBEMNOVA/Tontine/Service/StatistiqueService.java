package com.MBEMNOVA.Tontine.Service;


import com.MBEMNOVA.Tontine.DTO.StatistiqueMembreDTO;
import com.MBEMNOVA.Tontine.Entity.Adhesion;
import com.MBEMNOVA.Tontine.Entity.Cotisation;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Entity.Tour;
import com.MBEMNOVA.Tontine.Repository.CotisationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional(readOnly = true)
public class StatistiqueService {

    private final CotisationRepository cotisationRepository;
    private final AdhesionService adhesionService;
    private final TourService tourService;

    public StatistiqueService(
            CotisationRepository cotisationRepository,
            AdhesionService adhesionService,
            TourService tourService
    ) {
        this.cotisationRepository = cotisationRepository;
        this.adhesionService = adhesionService;
        this.tourService = tourService;
    }

    /**
     * Total collecté par la tontine.
     */
    public BigDecimal totalCollecte(Long tontineId) {
        return cotisationRepository.totalCollecteByTontine(
                tontineId,
                Cotisation.StatutCotisation.PAYE
        );
    }

    /**
     * Nombre de cotisations.
     */
    public long nombreCotisations(Long tontineId) {
        return cotisationRepository.findByTontineIdOrderByDatePaiementDesc(tontineId).size();
    }

    /**
     * Nombre de retards.
     */
    public long nombreRetards(Long tontineId) {
        return cotisationRepository.countByTontineIdAndStatut(
                tontineId,
                Cotisation.StatutCotisation.EN_RETARD
        );
    }

    /**
     * Taux de retard.
     */
    public double tauxRetard(Long tontineId) {
        long total = nombreCotisations(tontineId);
        if (total == 0) return 0;
        return ((double) nombreRetards(tontineId) / total) * 100;
    }

    /**
     * Statistiques de chaque membre.
     */
    public List<StatistiqueMembreDTO> statistiquesMembres(Long tontineId) {
        List<Adhesion> adhesions = adhesionService.findByTontine(tontineId);
        // Nombre de membres inscrits à la tontine : c'est ce montant, multiplié
        // par la cotisation unitaire, qu'un bénéficiaire touche à chaque tour versé
        // (tous les membres cotisent pour le bénéficiaire du cycle).
        int nombreMembres = adhesions.size();
        List<StatistiqueMembreDTO> resultats = new ArrayList<>();

        for (Adhesion adhesion : adhesions) {
            Membre membre = adhesion.getMembre();

            BigDecimal totalCotise = cotisationRepository.totalCotiseParMembre(
                    membre.getId(),
                    tontineId,
                    Cotisation.StatutCotisation.PAYE
            );

            BigDecimal totalRecu = calculerTotalRecu(membre, tontineId, nombreMembres);
            BigDecimal soldeNet = totalRecu.subtract(totalCotise);

            StatistiqueMembreDTO dto = StatistiqueMembreDTO.builder()
                    .membreId(membre.getId())
                    .nomMembre(membre.getPrenom() + " " + membre.getNom())
                    .totalCotise(totalCotise)
                    .totalRecu(totalRecu)
                    .soldeNet(soldeNet)
                    .build();

            resultats.add(dto);
        }

        return resultats;
    }

    /**
     * Total reçu par un membre au sein d'une tontine : pour chaque tour versé
     * dont il a été le bénéficiaire, il reçoit la cotisation unitaire multipliée
     * par le nombre de membres inscrits (chacun cotise pour le bénéficiaire du cycle).
     *
     * Bug corrigé : la version précédente multipliait par le nombre total de
     * tours déjà générés dans la tontine (tours.size()) au lieu du nombre de
     * membres inscrits, ce qui gonflait artificiellement le montant reçu dès
     * qu'un deuxième tour était généré.
     */
    private BigDecimal calculerTotalRecu(Membre membre, Long tontineId, int nombreMembres) {
        List<Tour> tours = tourService.findByTontine(tontineId);
        BigDecimal total = BigDecimal.ZERO;

        for (Tour tour : tours) {
            if (tour.getMembreBeneficiaire().getId().equals(membre.getId())
                    && tour.getStatut() == Tour.StatutTour.VERSE) {
                total = total.add(
                        tour.getTontine().getMontantCotisation()
                                .multiply(BigDecimal.valueOf(nombreMembres))
                );
            }
        }
        return total;
    }
}
