package com.MBEMNOVA.Tontine.Service;

import com.MBEMNOVA.Tontine.DTO.TontineDTO;
import com.MBEMNOVA.Tontine.Entity.Tontine;
import com.MBEMNOVA.Tontine.Repository.TontineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TontineService {

    private final TontineRepository tontineRepository;

    public TontineService(TontineRepository tontineRepository) {
        this.tontineRepository = tontineRepository;
    }

    /**
     * Liste de toutes les tontines.
     */
    @Transactional(readOnly = true)
    public List<Tontine> findAll() {
        return tontineRepository.findAll();
    }

    /**
     * Recherche d'une tontine.
     */
    @Transactional(readOnly = true)
    public Tontine findById(Long id) {
        return tontineRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Tontine introuvable avec l'id : " + id
                        )
                );
    }

    /**
     * Liste des tontines actives.
     */
    @Transactional(readOnly = true)
    public List<Tontine> findActiveTontines() {
        return tontineRepository.findByStatut(
                Tontine.StatutTontine.ACTIVE
        );
    }

    /**
     * Recherche par nom.
     */
    @Transactional(readOnly = true)
    public List<Tontine> search(String nom) {
        return tontineRepository.findByNomContainingIgnoreCase(nom);
    }

    /**
     * Création d'une tontine.
     */
    public Tontine create(TontineDTO dto) {

        Tontine tontine = new Tontine();

        tontine.setNom(dto.getNom());
        tontine.setMontantCotisation(dto.getMontantCotisation());
        tontine.setFrequence(dto.getFrequence());
        tontine.setDateDebut(dto.getDateDebut());

        tontine.setStatut(
                dto.getStatut() != null
                        ? dto.getStatut()
                        : Tontine.StatutTontine.ACTIVE
        );

        return tontineRepository.save(tontine);
    }

    /**
     * Modification d'une tontine.
     */
    public Tontine update(Long id, TontineDTO dto) {

        Tontine tontine = findById(id);

        tontine.setNom(dto.getNom());
        tontine.setMontantCotisation(dto.getMontantCotisation());
        tontine.setFrequence(dto.getFrequence());
        tontine.setDateDebut(dto.getDateDebut());
        tontine.setStatut(dto.getStatut());

        return tontineRepository.save(tontine);
    }

    /**
     * Suppression d'une tontine.
     */
    public void delete(Long id) {

        Tontine tontine = findById(id);

        tontineRepository.delete(tontine);
    }
}