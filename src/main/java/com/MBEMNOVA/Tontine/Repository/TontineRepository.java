package com.MBEMNOVA.Tontine.Repository;


import com.MBEMNOVA.Tontine.Entity.Tontine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TontineRepository extends JpaRepository<Tontine, Long> {

    List<Tontine> findByStatut(Tontine.StatutTontine statut);

    List<Tontine> findByNomContainingIgnoreCase(String nom);
}