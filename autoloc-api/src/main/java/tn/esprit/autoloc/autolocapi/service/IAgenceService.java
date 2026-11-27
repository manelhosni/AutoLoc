package tn.esprit.autoloc.autolocapi.service;

import tn.esprit.autoloc.autolocapi.dto.AgenceDTO;

import java.util.List;

public interface IAgenceService {
    AgenceDTO create(AgenceDTO agenceDTO);
    AgenceDTO findById(Long id);
    List<AgenceDTO> findAll();
    AgenceDTO update(Long id, AgenceDTO agenceDTO);
    void deleteById(Long id);
}
