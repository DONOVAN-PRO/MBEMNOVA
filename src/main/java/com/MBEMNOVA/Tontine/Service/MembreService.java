package com.MBEMNOVA.Tontine.Service;

import com.MBEMNOVA.Tontine.DTO.MembreDTO;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MembreService {

    private final MembreRepository membreRepository;

    public MembreService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    /**
     * Retourne tous les membres.
     */
    @Transactional(readOnly = true)
    public List<Membre> findAll() {
        return membreRepository.findAll();
    }

    /**
     * Recherche un membre par son identifiant.
     */
    @Transactional(readOnly = true)
    public Membre findById(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Membre introuvable avec l'id : " + id)
                );
    }

    /**
     * Recherche un membre par email.
     */
    @Transactional(readOnly = true)
    public Membre findByEmail(String email) {
        return membreRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Aucun membre trouvé avec l'email : " + email
                        )
                );
    }

    /**
     * Création d'un membre.
     */
    public Membre create(MembreDTO dto) {

        if (membreRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Un membre existe déjà avec cet email."
            );
        }

        Membre membre = new Membre();

        membre.setNom(dto.getNom());
        membre.setPrenom(dto.getPrenom());
        membre.setEmail(dto.getEmail());
        membre.setTelephone(dto.getTelephone());
        membre.setDateAdhesion(dto.getDateAdhesion());
        membre.setStatut(
                dto.getStatut() != null
                        ? dto.getStatut()
                        : Membre.StatutMembre.ACTIF
        );

        return membreRepository.save(membre);
    }

    /**
     * Modification d'un membre.
     */
    public Membre update(Long id, MembreDTO dto) {

        Membre membre = findById(id);

        if (!membre.getEmail().equals(dto.getEmail())
                && membreRepository.existsByEmail(dto.getEmail())) {

            throw new RuntimeException(
                    "Un autre membre utilise déjà cet email."
            );
        }

        membre.setNom(dto.getNom());
        membre.setPrenom(dto.getPrenom());
        membre.setEmail(dto.getEmail());
        membre.setTelephone(dto.getTelephone());
        membre.setDateAdhesion(dto.getDateAdhesion());
        membre.setStatut(dto.getStatut());

        return membreRepository.save(membre);
    }

    /**
     * Suppression d'un membre.
     */
    public void delete(Long id) {

        Membre membre = findById(id);

        membreRepository.delete(membre);
    }
}