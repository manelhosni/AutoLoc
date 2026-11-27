package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.autolocapi.domain.Equipement;
import tn.esprit.autoloc.autolocapi.domain.Vehicule;
import tn.esprit.autoloc.autolocapi.dto.EquipementDTO;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.EquipementMapper;
import tn.esprit.autoloc.autolocapi.mapper.VehiculeMapper;
import tn.esprit.autoloc.autolocapi.repository.IEquipementRepository;
import tn.esprit.autoloc.autolocapi.repository.IVehiculeRepository;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final EquipementMapper equipementMapper;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public EquipementDTO create(EquipementDTO equipementDTO) {
        if (equipementDTO.getIdEquipement() != null) {
            throw new IllegalArgumentException("Un nouvel équipement ne doit pas avoir d'identifiant");
        }
        validerEquipement(equipementDTO);
        Equipement entity = equipementMapper.toEntity(equipementDTO);
        Equipement saved = equipementRepository.save(entity);
        return equipementMapper.toDTO(saved);
    }

    @Override
    public EquipementDTO findById(Long id) {
        return equipementRepository.findById(id)
                .map(equipementMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }

    @Override
    public List<EquipementDTO> findAll() {
        return equipementMapper.toDTOList(equipementRepository.findAll());
    }

    @Override
    public EquipementDTO update(Long id, EquipementDTO equipementDTO) {
        Equipement existant = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
        validerEquipement(equipementDTO);
        existant.setLibelle(equipementDTO.getLibelle());
        Equipement updated = equipementRepository.save(existant);
        return equipementMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipement", id);
        }
        equipementRepository.deleteById(id);
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

    private void validerEquipement(EquipementDTO equipementDTO) {
        if (equipementDTO.getLibelle() == null || equipementDTO.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de l'équipement ne peut pas être vide");
        }
    }
}
