package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.autolocapi.domain.Agence;
import tn.esprit.autoloc.autolocapi.dto.AgenceDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.AgenceMapper;
import tn.esprit.autoloc.autolocapi.repository.IAgenceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final AgenceMapper agenceMapper;

    @Override
    public AgenceDTO create(AgenceDTO agenceDTO) {
        if (agenceDTO.getIdAgence() != null) {
            throw new IllegalArgumentException("Une nouvelle agence ne doit pas avoir d'identifiant");
        }
        validerAgence(agenceDTO);
        Agence entity = agenceMapper.toEntity(agenceDTO);
        Agence saved = agenceRepository.save(entity);
        return agenceMapper.toDTO(saved);
    }

    @Override
    public AgenceDTO findById(Long id) {
        return agenceRepository.findById(id)
                .map(agenceMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    public List<AgenceDTO> findAll() {
        return agenceMapper.toDTOList(agenceRepository.findAll());
    }

    @Override
    public AgenceDTO update(Long id, AgenceDTO agenceDTO) {
        Agence existant = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        validerAgence(agenceDTO);
        existant.setNom(agenceDTO.getNom());
        existant.setVille(agenceDTO.getVille());
        existant.setAdresse(agenceDTO.getAdresse());
        existant.setTelephone(agenceDTO.getTelephone());
        Agence updated = agenceRepository.save(existant);
        return agenceMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private void validerAgence(AgenceDTO agenceDTO) {
        if (agenceDTO.getNom() == null || agenceDTO.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'agence ne peut pas être vide");
        }
    }
}
