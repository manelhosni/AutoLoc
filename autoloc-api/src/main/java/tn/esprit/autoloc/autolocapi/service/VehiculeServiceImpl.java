package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.autolocapi.domain.Agence;
import tn.esprit.autoloc.autolocapi.domain.Equipement;
import tn.esprit.autoloc.autolocapi.domain.Maintenance;
import tn.esprit.autoloc.autolocapi.domain.Vehicule;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.VehiculeMapper;
import tn.esprit.autoloc.autolocapi.repository.IAgenceRepository;
import tn.esprit.autoloc.autolocapi.repository.IEquipementRepository;
import tn.esprit.autoloc.autolocapi.repository.IMaintenanceRepository;
import tn.esprit.autoloc.autolocapi.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeDTO create(VehiculeDTO vehiculeDTO) {
        if (vehiculeDTO.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        validerVehicule(vehiculeDTO);
        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDTO);
        if (vehiculeDTO.getAgence() != null && vehiculeDTO.getAgence().getIdAgence() != null) {
            Agence agence = agenceRepository.findById(vehiculeDTO.getAgence().getIdAgence())
                    .orElse(vehicule.getAgence());
            vehicule.setAgence(agence);
        }
        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDTO(saved);
    }

    @Override
    public VehiculeDTO findById(Long id) {
        return vehiculeRepository.findById(id)
                .map(vehiculeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<VehiculeDTO> findAll() {
        return vehiculeMapper.toDTOList(vehiculeRepository.findAll());
    }

    @Override
    public VehiculeDTO update(Long id, VehiculeDTO vehiculeDTO) {
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        validerVehicule(vehiculeDTO);
        existant.setImmatriculation(vehiculeDTO.getImmatriculation());
        existant.setMarque(vehiculeDTO.getMarque());
        existant.setModele(vehiculeDTO.getModele());
        existant.setTarifJournalier(vehiculeDTO.getTarifJournalier());
        existant.setCategorie(vehiculeDTO.getCategorie());
        existant.setStatut(vehiculeDTO.getStatut());
        if (vehiculeDTO.getAgence() != null && vehiculeDTO.getAgence().getIdAgence() != null) {
            Agence agence = agenceRepository.findById(vehiculeDTO.getAgence().getIdAgence()).orElse(null);
            existant.setAgence(agence);
        } else {
            existant.setAgence(null);
        }
        Vehicule updated = vehiculeRepository.save(existant);
        return vehiculeMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));

        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipement));

        if (equipement.getVehicules() == null) {
            equipement.setVehicules(new HashSet<>());
        }
        equipement.getVehicules().add(vehicule);
        equipementRepository.save(equipement);

        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new HashSet<>());
        }
        vehicule.getEquipements().add(equipement);
        Vehicule updated = vehiculeRepository.save(vehicule);

        return vehiculeMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation) {
        Vehicule vehicule = vehiculeRepository.findByImmatriculation(immatriculation)
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'immatriculation " + immatriculation));

        Maintenance maintenance = maintenanceRepository.findFirstByDescriptionIgnoreCase(description)
                .or(() -> maintenanceRepository.findFirstByDescription(description))
                .orElseGet(() -> {
                    Maintenance m = new Maintenance();
                    m.setDescription(description);
                    m.setDateDebut(LocalDate.now());
                    m.setDateFin(LocalDate.now());
                    return m;
                });

        maintenance.setVehicule(vehicule);
        maintenanceRepository.save(maintenance);

        if (vehicule.getMaintenances() == null) {
            vehicule.setMaintenances(new HashSet<>());
        }
        vehicule.getMaintenances().add(maintenance);
        Vehicule updated = vehiculeRepository.save(vehicule);

        return vehiculeMapper.toDTO(updated);
    }

    private void validerVehicule(VehiculeDTO vehiculeDTO) {
        if (vehiculeDTO.getTarifJournalier() != null && vehiculeDTO.getTarifJournalier().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif journalier ne peut pas être négatif");
        }
    }
}
