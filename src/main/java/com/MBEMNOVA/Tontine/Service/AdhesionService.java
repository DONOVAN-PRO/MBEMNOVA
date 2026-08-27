package com.MBEMNOVA.Tontine.Service;

import com.MBEMNOVA.Tontine.DTO.AdhesionDTO;
import com.MBEMNOVA.Tontine.Entity.Adhesion;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Entity.Tontine;
import com.MBEMNOVA.Tontine.Repository.AdhesionRepository;
import com.MBEMNOVA.Tontine.Repository.MembreRepository;
import com.MBEMNOVA.Tontine.Repository.TontineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdhesionService {

    private final AdhesionRepository adhesionRepository;
    private final MembreRepository membreRepository;
    private final TontineRepository tontineRepository;

    public AdhesionService(
            AdhesionRepository adhesionRepository,
            MembreRepository membreRepository,
            TontineRepository tontineRepository
    ) {
        this.adhesionRepository = adhesionRepository;
        this.membreRepository = membreRepository;
        this.tontineRepository = tontineRepository;
    }

    /**
     * Liste les adhésions d'une tontine
     * dans l'ordre de passage.
     */
    @Transactional(readOnly = true)
    public List<Adhesion> findByTontine(Long tontineId) {

        return adhesionRepository
                .findByTontineIdOrderByOrdreTourAsc(tontineId);
    }

    /**
     * Inscription d'un membre à une tontine.
     */
    public Adhesion create(AdhesionDTO dto) {

        Membre membre = membreRepository.findById(dto.getMembreId())
                .orElseThrow(() ->
                        new RuntimeException("Membre introuvable.")
                );

        Tontine tontine = tontineRepository.findById(dto.getTontineId())
                .orElseThrow(() ->
                        new RuntimeException("Tontine introuvable.")
                );

        /*
         * Vérifie que le membre n'est pas déjà
         * inscrit à cette tontine.
         */
        if (adhesionRepository.existsByTontineIdAndMembreId(
                dto.getTontineId(),
                dto.getMembreId()
        )) {
            throw new RuntimeException(
                    "Ce membre est déjà inscrit à cette tontine."
            );
        }

        /*
         * Vérifie que l'ordre de passage
         * n'est pas déjà utilisé.
         */
        if (adhesionRepository.existsByTontineIdAndOrdreTour(
                dto.getTontineId(),
                dto.getOrdreTour()
        )) {
            throw new RuntimeException(
                    "Cet ordre de passage est déjà utilisé."
            );
        }

        Adhesion adhesion = new Adhesion();

        adhesion.setMembre(membre);
        adhesion.setTontine(tontine);
        adhesion.setOrdreTour(dto.getOrdreTour());
        adhesion.setDateAdhesion(dto.getDateAdhesion());

        return adhesionRepository.save(adhesion);
    }

    /**
     * Suppression d'une adhésion.
     */
    public void delete(Long id) {

        Adhesion adhesion = adhesionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Adhésion introuvable."
                        )
                );

        adhesionRepository.delete(adhesion);
    }

    /**
     * Retire un membre d'une tontine.
     */
    public void removeMemberFromTontine(
            Long tontineId,
            Long membreId
    ) {

        Adhesion adhesion = adhesionRepository
                .findByTontineIdAndMembreId(
                        tontineId,
                        membreId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ce membre n'est pas inscrit à cette tontine."
                        )
                );

        adhesionRepository.delete(adhesion);
    }
}