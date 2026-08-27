package com.MBEMNOVA.Tontine.Repository;

import com.MBEMNOVA.Tontine.Entity.Cotisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CotisationRepository extends JpaRepository<Cotisation, Long> {

    List<Cotisation> findByTontineIdOrderByDatePaiementDesc(Long tontineId);

    List<Cotisation> findByMembreIdOrderByDatePaiementDesc(Long membreId);

    List<Cotisation> findByTontineIdAndCycle(Long tontineId, Integer cycle);

    // ✅ Une seule méthode pour filtrer par statut
    List<Cotisation> findByStatut(Cotisation.StatutCotisation statut);

    long countByTontineIdAndStatut(Long tontineId, Cotisation.StatutCotisation statut);

    @Query("""
        SELECT COALESCE(SUM(c.montant), 0)
        FROM Cotisation c
        WHERE c.tontine.id = :tontineId
        AND c.statut = :statut
    """)
    BigDecimal totalCollecteByTontine(@Param("tontineId") Long tontineId,
                                      @Param("statut") Cotisation.StatutCotisation statut);

    @Query("""
        SELECT COALESCE(SUM(c.montant), 0)
        FROM Cotisation c
        WHERE c.membre.id = :membreId
        AND c.tontine.id = :tontineId
        AND c.statut = :statut
    """)
    BigDecimal totalCotiseParMembre(@Param("membreId") Long membreId,
                                    @Param("tontineId") Long tontineId,
                                    @Param("statut") Cotisation.StatutCotisation statut);
}
