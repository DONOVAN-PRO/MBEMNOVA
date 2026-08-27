package com.MBEMNOVA.Tontine.Service;

import com.MBEMNOVA.Tontine.Entity.Adhesion;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Entity.Tontine;
import com.MBEMNOVA.Tontine.Entity.Tour;
import com.MBEMNOVA.Tontine.Repository.AdhesionRepository;
import com.MBEMNOVA.Tontine.Repository.TontineRepository;
import com.MBEMNOVA.Tontine.Repository.TourRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TourService {

    private final TourRepository tourRepository;
    private final AdhesionRepository adhesionRepository;
    private final TontineRepository tontineRepository;

    public TourService(
            TourRepository tourRepository,
            AdhesionRepository adhesionRepository,
            TontineRepository tontineRepository
    ) {
        this.tourRepository = tourRepository;
        this.adhesionRepository = adhesionRepository;
        this.tontineRepository = tontineRepository;
    }

    /**
     * Liste les tours d'une tontine.
     */
    @Transactional(readOnly = true)
    public List<Tour> findByTontine(Long tontineId) {

        return tourRepository
                .findByTontineIdOrderByNumeroCycleAsc(tontineId);
    }

    /**
     * Recherche un tour.
     */
    @Transactional(readOnly = true)
    public Tour findById(Long id) {

        return tourRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tour introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Génère le prochain tour automatiquement.
     */
    public Tour genererProchainTour(Long tontineId) {

        Tontine tontine = tontineRepository.findById(tontineId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tontine introuvable."
                        )
                );

        /*
         * Récupération des membres dans l'ordre
         * défini lors de leur adhésion.
         */
        List<Adhesion> adhesions =
                adhesionRepository.findByTontineIdOrderByOrdreTourAsc(
                        tontineId
                );

        if (adhesions.isEmpty()) {
            throw new RuntimeException(
                    "Impossible de générer un tour : " +
                            "aucun membre n'est inscrit."
            );
        }

        /*
         * Nombre de tours déjà créés.
         */
        List<Tour> tours =
                tourRepository.findByTontineIdOrderByNumeroCycleAsc(
                        tontineId
                );

        int prochainCycle = tours.size() + 1;

        /*
         * Détermination du membre bénéficiaire.
         *
         * Cycle 1 → membre ordre 1
         * Cycle 2 → membre ordre 2
         * Cycle 3 → membre ordre 3
         * etc.
         *
         * Lorsque tous les membres sont passés,
         * on recommence au premier.
         */
        int index = (prochainCycle - 1) % adhesions.size();

        Membre beneficiaire =
                adhesions.get(index).getMembre();

        /*
         * Vérifie qu'un tour pour ce cycle
         * n'existe pas déjà.
         */
        if (tourRepository.existsByTontineIdAndNumeroCycle(
                tontineId,
                prochainCycle
        )) {
            throw new RuntimeException(
                    "Le tour du cycle " +
                            prochainCycle +
                            " existe déjà."
            );
        }

        Tour tour = new Tour();

        tour.setTontine(tontine);
        tour.setNumeroCycle(prochainCycle);
        tour.setMembreBeneficiaire(beneficiaire);
        tour.setDatePrevue(
                calculerDatePrevue(
                        tontine,
                        prochainCycle
                )
        );
        tour.setStatut(Tour.StatutTour.A_VENIR);

        return tourRepository.save(tour);
    }

    /**
     * Calcule la date prévue du tour.
     */
    private LocalDate calculerDatePrevue(
            Tontine tontine,
            int numeroCycle
    ) {

        LocalDate dateDebut = tontine.getDateDebut();

        if (tontine.getFrequence()
                == Tontine.Frequence.HEBDOMADAIRE) {

            return dateDebut.plusWeeks(numeroCycle - 1L);

        } else {

            return dateDebut.plusMonths(numeroCycle - 1L);
        }
    }

    /**
     * Marque un tour comme versé.
     */
    public Tour verser(Long id) {

        Tour tour = findById(id);

        if (tour.getStatut() == Tour.StatutTour.VERSE) {
            throw new RuntimeException(
                    "Ce tour est déjà marqué comme versé."
            );
        }

        tour.setStatut(Tour.StatutTour.VERSE);

        return tourRepository.save(tour);
    }

    /**
     * Retourne le prochain bénéficiaire.
     */
    @Transactional(readOnly = true)
    public Membre getProchainBeneficiaire(Long tontineId) {

        Tour prochainTour =
                tourRepository
                        .findFirstByTontineIdAndStatutOrderByNumeroCycleAsc(
                                tontineId,
                                Tour.StatutTour.A_VENIR
                        )
                        .orElse(null);

        if (prochainTour == null) {
            return null;
        }

        return prochainTour.getMembreBeneficiaire();
    }

    @Transactional(readOnly = true)
    public List<Tour> getHistorique(Long tontineId) {

        return tourRepository
                .findByTontineIdOrderByNumeroCycleAsc(tontineId);
    }
}
