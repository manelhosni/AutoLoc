package tn.esprit.autoloc.autolocapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.autolocapi.domain.Agence;
import tn.esprit.autoloc.autolocapi.domain.Employe;
import tn.esprit.autoloc.autolocapi.dto.EmployeDTO;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.EmployeMapper;
import tn.esprit.autoloc.autolocapi.repository.IAgenceRepository;
import tn.esprit.autoloc.autolocapi.repository.IEmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Override
    public EmployeDTO create(EmployeDTO employeDTO) {
        if (employeDTO.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        validerEmploye(employeDTO);
        Employe employe = employeMapper.toEntity(employeDTO);
        if (employeDTO.getAgence() != null && employeDTO.getAgence().getIdAgence() != null) {
            Agence agence = agenceRepository.findById(employeDTO.getAgence().getIdAgence())
                    .orElse(employe.getAgence());
            employe.setAgence(agence);
        }
        Employe saved = employeRepository.save(employe);
        return employeMapper.toDTO(saved);
    }

    @Override
    public EmployeDTO findById(Long id) {
        return employeRepository.findById(id)
                .map(employeMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    @Override
    public List<EmployeDTO> findAll() {
        return employeMapper.toDTOList(employeRepository.findAll());
    }

    @Override
    public EmployeDTO update(Long id, EmployeDTO employeDTO) {
        Employe existant = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
        validerEmploye(employeDTO);
        existant.setNom(employeDTO.getNom());
        existant.setPrenom(employeDTO.getPrenom());
        existant.setRole(employeDTO.getRole());
        if (employeDTO.getAgence() != null && employeDTO.getAgence().getIdAgence() != null) {
            Agence agence = agenceRepository.findById(employeDTO.getAgence().getIdAgence())
                    .orElse(null);
            existant.setAgence(agence);
        } else {
            existant.setAgence(null);
        }
        Employe updated = employeRepository.save(existant);
        return employeMapper.toDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private void validerEmploye(EmployeDTO employeDTO) {
        if (employeDTO.getNom() == null || employeDTO.getNom().trim().isEmpty() || employeDTO.getRole() == null) {
            throw new IllegalArgumentException("Le nom et le rôle de l'employé sont obligatoires");
        }
    }
}
