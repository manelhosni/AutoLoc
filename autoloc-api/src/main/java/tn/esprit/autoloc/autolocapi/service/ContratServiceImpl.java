package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.autolocapi.domain.Contrat;
import tn.esprit.autoloc.autolocapi.domain.Reservation;
import tn.esprit.autoloc.autolocapi.dto.ContratDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.ContratMapper;
import tn.esprit.autoloc.autolocapi.repository.IContratRepository;
import tn.esprit.autoloc.autolocapi.repository.IReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    public ContratDTO create(ContratDTO contratDTO) {
        if (contratDTO.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        validerContrat(contratDTO);
        Contrat contrat = contratMapper.toEntity(contratDTO);
        Contrat saved = contratRepository.save(contrat);
        return contratMapper.toDTO(saved);
    }

    @Override
    public ContratDTO findById(Long id) {
        return contratRepository.findById(id)
                .map(contratMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<ContratDTO> findAll() {
        return contratMapper.toDTOList(contratRepository.findAll());
    }

    @Override
    public ContratDTO update(Long id, ContratDTO contratDTO) {
        Contrat existant = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        validerContrat(contratDTO);
        existant.setDateSignature(contratDTO.getDateSignature());
        existant.setMontantTotal(contratDTO.getMontantTotal());
        existant.setValide(contratDTO.isValide());
        Contrat updated = contratRepository.save(existant);
        return contratMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        Contrat contrat = contratRepository.findFirstByDateSignature(dateSignature)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable avec la date de signature " + dateSignature));

        Reservation reservation = reservationRepository.findFirstByDateDebutAndDateFin(dateDebut, dateFin)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable pour la période du " + dateDebut + " au " + dateFin));

        reservation.setContrat(contrat);
        reservationRepository.save(reservation);

        contrat.setReservation(reservation);
        Contrat updated = contratRepository.save(contrat);

        return contratMapper.toDTO(updated);
    }

    private void validerContrat(ContratDTO contratDTO) {
        if (contratDTO.getMontantTotal() != null && contratDTO.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
    }
}
