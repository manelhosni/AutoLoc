package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.dto.EquipementDTO;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;

import java.util.List;

public interface IEquipementService {
    EquipementDTO create(EquipementDTO equipementDTO);
    EquipementDTO findById(Long id);
    List<EquipementDTO> findAll();
    EquipementDTO update(Long id, EquipementDTO equipementDTO);
    void deleteById(Long id);

    VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement);
}
