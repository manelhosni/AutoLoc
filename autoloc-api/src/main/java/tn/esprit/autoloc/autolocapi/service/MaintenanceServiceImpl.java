package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.autolocapi.domain.Maintenance;
import tn.esprit.autoloc.autolocapi.domain.Vehicule;
import tn.esprit.autoloc.autolocapi.dto.MaintenanceDTO;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.MaintenanceMapper;
import tn.esprit.autoloc.autolocapi.mapper.VehiculeMapper;
import tn.esprit.autoloc.autolocapi.repository.IMaintenanceRepository;
import tn.esprit.autoloc.autolocapi.repository.IVehiculeRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public MaintenanceDTO create(MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO.getIdMaintenance() != null) {
            throw new IllegalArgumentException("Une nouvelle maintenance ne doit pas avoir d'identifiant");
        }
        validerMaintenance(maintenanceDTO);
        Maintenance maintenance = maintenanceMapper.toEntity(maintenanceDTO);
        if (maintenanceDTO.getVehicule() != null && maintenanceDTO.getVehicule().getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(maintenanceDTO.getVehicule().getIdVehicule())
                    .orElse(maintenance.getVehicule());
            maintenance.setVehicule(vehicule);
        }
        Maintenance saved = maintenanceRepository.save(maintenance);
        return maintenanceMapper.toDTO(saved);
    }

    @Override
    public MaintenanceDTO findById(Long id) {
        return maintenanceRepository.findById(id)
                .map(maintenanceMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    @Override
    public List<MaintenanceDTO> findAll() {
        return maintenanceMapper.toDTOList(maintenanceRepository.findAll());
    }

    @Override
    public MaintenanceDTO update(Long id, MaintenanceDTO maintenanceDTO) {
        Maintenance existant = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        validerMaintenance(maintenanceDTO);
        existant.setDateDebut(maintenanceDTO.getDateDebut());
        existant.setDateFin(maintenanceDTO.getDateFin());
        existant.setDescription(maintenanceDTO.getDescription());
        if (maintenanceDTO.getVehicule() != null && maintenanceDTO.getVehicule().getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(maintenanceDTO.getVehicule().getIdVehicule()).orElse(null);
            existant.setVehicule(vehicule);
        } else {
            existant.setVehicule(null);
        }
        Maintenance updated = maintenanceRepository.save(existant);
        return maintenanceMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
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

    private void validerMaintenance(MaintenanceDTO maintenanceDTO) {
        if (maintenanceDTO.getDateDebut() != null && maintenanceDTO.getDateFin() != null
                && maintenanceDTO.getDateFin().isBefore(maintenanceDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
