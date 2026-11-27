package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.autolocapi.domain.Client;
import tn.esprit.autoloc.autolocapi.domain.Contrat;
import tn.esprit.autoloc.autolocapi.domain.Paiement;
import tn.esprit.autoloc.autolocapi.domain.Reservation;
import tn.esprit.autoloc.autolocapi.domain.StatutReservation;
import tn.esprit.autoloc.autolocapi.domain.Vehicule;
import tn.esprit.autoloc.autolocapi.dto.ContratDTO;
import tn.esprit.autoloc.autolocapi.dto.PaiementDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationResponseDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.ContratMapper;
import tn.esprit.autoloc.autolocapi.mapper.PaiementMapper;
import tn.esprit.autoloc.autolocapi.mapper.ReservationMapper;
import tn.esprit.autoloc.autolocapi.repository.IClientRepository;
import tn.esprit.autoloc.autolocapi.repository.IContratRepository;
import tn.esprit.autoloc.autolocapi.repository.IReservationRepository;
import tn.esprit.autoloc.autolocapi.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IContratRepository contratRepository;
    private final ReservationMapper reservationMapper;
    private final ContratMapper contratMapper;
    private final PaiementMapper paiementMapper;

    @Override
    public ReservationDTO create(ReservationDTO reservationDTO) {
        if (reservationDTO.getIdReservation() != null) {
            throw new IllegalArgumentException("Une nouvelle réservation ne doit pas avoir d'identifiant");
        }
        validerReservation(reservationDTO);
        Reservation reservation = reservationMapper.toEntity(reservationDTO);

        if (reservationDTO.getClient() != null && reservationDTO.getClient().getIdClient() != null) {
            Client client = clientRepository.findById(reservationDTO.getClient().getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", reservationDTO.getClient().getIdClient()));
            reservation.setClient(client);
        } else {
            reservation.setClient(null);
        }

        if (reservationDTO.getVehicule() != null && reservationDTO.getVehicule().getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(reservationDTO.getVehicule().getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", reservationDTO.getVehicule().getIdVehicule()));
            reservation.setVehicule(vehicule);
        } else {
            reservation.setVehicule(null);
        }

        if (reservationDTO.getContrat() != null && reservationDTO.getContrat().getIdContrat() != null) {
            Contrat contrat = contratRepository.findById(reservationDTO.getContrat().getIdContrat())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", reservationDTO.getContrat().getIdContrat()));
            reservation.setContrat(contrat);
        } else {
            reservation.setContrat(null);
        }

        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toDTO(saved);
    }

    @Override
    public ReservationDTO findById(Long id) {
        return reservationRepository.findById(id)
                .map(reservationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    public List<ReservationDTO> findAll() {
        return reservationMapper.toDTOList(reservationRepository.findAll());
    }

    @Override
    public ReservationDTO update(Long id, ReservationDTO reservationDTO) {
        Reservation existant = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        validerReservation(reservationDTO);
        existant.setDateDebut(reservationDTO.getDateDebut());
        existant.setDateFin(reservationDTO.getDateFin());
        existant.setStatut(reservationDTO.getStatut());

        if (reservationDTO.getClient() != null && reservationDTO.getClient().getIdClient() != null) {
            Client client = clientRepository.findById(reservationDTO.getClient().getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", reservationDTO.getClient().getIdClient()));
            existant.setClient(client);
        } else {
            existant.setClient(null);
        }

        if (reservationDTO.getVehicule() != null && reservationDTO.getVehicule().getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(reservationDTO.getVehicule().getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", reservationDTO.getVehicule().getIdVehicule()));
            existant.setVehicule(vehicule);
        } else {
            existant.setVehicule(null);
        }

        if (reservationDTO.getContrat() != null && reservationDTO.getContrat().getIdContrat() != null) {
            Contrat contrat = contratRepository.findById(reservationDTO.getContrat().getIdContrat())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", reservationDTO.getContrat().getIdContrat()));
            existant.setContrat(contrat);
        } else {
            existant.setContrat(null);
        }

        Reservation updated = reservationRepository.save(existant);
        return reservationMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", idReservation));

        Client client = clientRepository.findByNomAndPrenom(nom, prenom)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec le nom '" + nom + "' et le prénom '" + prenom + "'"));

        reservation.setClient(client);
        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponseDTO(saved);
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

    @Override
    @Transactional
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Les données de la réservation et du contrat sont obligatoires");
        }
        if (requestDTO.getDateDebut() == null || requestDTO.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin de réservation sont obligatoires");
        }
        if (requestDTO.getDateFin().isBefore(requestDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // 1. Résolution des informations du Contrat
        LocalDate dateSig = requestDTO.getDateSignature();
        BigDecimal montant = requestDTO.getMontantTotal();
        Boolean valide = requestDTO.getValide();
        Set<PaiementDTO> paiementsDTO = requestDTO.getPaiements();

        if (requestDTO.getContrat() != null) {
            if (dateSig == null) {
                dateSig = requestDTO.getContrat().getDateSignature();
            }
            if (montant == null) {
                montant = requestDTO.getContrat().getMontantTotal();
            }
            if (valide == null) {
                valide = requestDTO.getContrat().getValide();
            }
            if (paiementsDTO == null || paiementsDTO.isEmpty()) {
                paiementsDTO = requestDTO.getContrat().getPaiements();
            }
        }

        if (dateSig == null) {
            dateSig = LocalDate.now();
        }
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }
        if (montant.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
        if (valide == null) {
            valide = true;
        }

        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSig);
        contrat.setMontantTotal(montant);
        contrat.setValide(valide);

        if (paiementsDTO != null && !paiementsDTO.isEmpty()) {
            Set<Paiement> paiements = new HashSet<>();
            for (PaiementDTO pDto : paiementsDTO) {
                Paiement p = paiementMapper.toEntity(pDto);
                p.setContrat(contrat);
                paiements.add(p);
            }
            contrat.setPaiements(paiements);
        }

        Contrat savedContrat = contratRepository.save(contrat);

        // 2. Création de la Réservation
        Reservation reservation = new Reservation();
        reservation.setDateDebut(requestDTO.getDateDebut());
        reservation.setDateFin(requestDTO.getDateFin());
        reservation.setStatut(requestDTO.getStatut() != null ? requestDTO.getStatut() : StatutReservation.CONFIRMEE);
        reservation.setContrat(savedContrat);

        Long clientId = requestDTO.getIdClient();
        if (clientId == null && requestDTO.getClient() != null) {
            clientId = requestDTO.getClient().getIdClient();
        }
        if (clientId != null) {
            final Long idCli = clientId;
            Client client = clientRepository.findById(idCli)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", idCli));
            reservation.setClient(client);
        }

        Long vehiculeId = requestDTO.getIdVehicule();
        if (vehiculeId == null && requestDTO.getVehicule() != null) {
            vehiculeId = requestDTO.getVehicule().getIdVehicule();
        }
        if (vehiculeId != null) {
            final Long idVeh = vehiculeId;
            Vehicule vehicule = vehiculeRepository.findById(idVeh)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVeh));
            reservation.setVehicule(vehicule);
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        savedContrat.setReservation(savedReservation);

        return reservationMapper.toResponseDTO(savedReservation);
    }

    private void validerReservation(ReservationDTO reservationDTO) {
        if (reservationDTO.getDateDebut() != null && reservationDTO.getDateFin() != null
                && reservationDTO.getDateFin().isBefore(reservationDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
