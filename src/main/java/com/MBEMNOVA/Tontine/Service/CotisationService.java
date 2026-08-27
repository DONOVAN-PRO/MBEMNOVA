package com.MBEMNOVA.Tontine.Service;

import com.MBEMNOVA.Tontine.DTO.CotisationDTO;
import com.MBEMNOVA.Tontine.Entity.Cotisation;
import com.MBEMNOVA.Tontine.Entity.Membre;
import com.MBEMNOVA.Tontine.Entity.Tontine;
import com.MBEMNOVA.Tontine.Repository.CotisationRepository;
import com.MBEMNOVA.Tontine.Repository.MembreRepository;
import com.MBEMNOVA.Tontine.Repository.TontineRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CotisationService {

    private final CotisationRepository cotisationRepository;
    private final MembreRepository membreRepository;
    private final TontineRepository tontineRepository;

    public CotisationService(
            CotisationRepository cotisationRepository,
            MembreRepository membreRepository,
            TontineRepository tontineRepository
    ) {
        this.cotisationRepository = cotisationRepository;
        this.membreRepository = membreRepository;
        this.tontineRepository = tontineRepository;
    }

    @Transactional(readOnly = true)
    public List<Cotisation> findByTontine(Long tontineId) {
        return cotisationRepository.findByTontineIdOrderByDatePaiementDesc(tontineId);
    }

    public Cotisation create(CotisationDTO dto) {
        Membre membre = membreRepository.findById(dto.getMembreId())
                .orElseThrow(() -> new RuntimeException("Membre introuvable."));
        Tontine tontine = tontineRepository.findById(dto.getTontineId())
                .orElseThrow(() -> new RuntimeException("Tontine introuvable."));

        if (dto.getMontant() == null || dto.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Le montant de la cotisation doit être supérieur à 0.");
        }

        Cotisation cotisation = new Cotisation();
        cotisation.setMembre(membre);
        cotisation.setTontine(tontine);
        cotisation.setMontant(dto.getMontant());
        cotisation.setDatePaiement(dto.getDatePaiement());
        cotisation.setCycle(dto.getCycle());
        cotisation.setStatut(dto.getStatut() != null ? dto.getStatut() : Cotisation.StatutCotisation.PAYE);

        return cotisationRepository.save(cotisation);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCollecte(Long tontineId) {
        return cotisationRepository.totalCollecteByTontine(tontineId, Cotisation.StatutCotisation.PAYE);
    }

    @Transactional(readOnly = true)
    public long getNombreRetards(Long tontineId) {
        return cotisationRepository.countByTontineIdAndStatut(tontineId, Cotisation.StatutCotisation.EN_RETARD);
    }

    @Transactional(readOnly = true)
    public long getNombreCotisations(Long tontineId) {
        return cotisationRepository.findByTontineIdOrderByDatePaiementDesc(tontineId).size();
    }

    @Transactional(readOnly = true)
    public double getTauxRetard(Long tontineId) {
        long total = getNombreCotisations(tontineId);
        if (total == 0) return 0.0;
        long retards = getNombreRetards(tontineId);
        return ((double) retards / total) * 100;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalCotiseParMembre(Long membreId, Long tontineId) {
        return cotisationRepository.totalCotiseParMembre(membreId, tontineId, Cotisation.StatutCotisation.PAYE);
    }
}
