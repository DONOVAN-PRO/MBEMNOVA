package com.MBEMNOVA.Tontine.Repository;

import com.MBEMNOVA.Tontine.Entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long> {

    List<Tour> findByTontineIdOrderByNumeroCycleAsc(Long tontineId);

    Optional<Tour> findByTontineIdAndNumeroCycle(
            Long tontineId,
            Integer numeroCycle
    );

    Optional<Tour> findFirstByTontineIdAndStatutOrderByNumeroCycleAsc(
            Long tontineId,
            Tour.StatutTour statut
    );

    List<Tour> findByMembreBeneficiaireId(
            Long membreId
    );

    boolean existsByTontineIdAndNumeroCycle(
            Long tontineId,
            Integer numeroCycle
    );
}