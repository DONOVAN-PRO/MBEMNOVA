package com.MBEMNOVA.Tontine.Repository;

import com.MBEMNOVA.Tontine.Entity.Adhesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdhesionRepository extends JpaRepository<Adhesion, Long> {

    List<Adhesion> findByTontineIdOrderByOrdreTourAsc(Long tontineId);

    List<Adhesion> findByMembreId(Long membreId);

    Optional<Adhesion> findByTontineIdAndMembreId(
            Long tontineId,
            Long membreId
    );

    boolean existsByTontineIdAndMembreId(
            Long tontineId,
            Long membreId
    );

    boolean existsByTontineIdAndOrdreTour(
            Long tontineId,
            Integer ordreTour
    );
}
