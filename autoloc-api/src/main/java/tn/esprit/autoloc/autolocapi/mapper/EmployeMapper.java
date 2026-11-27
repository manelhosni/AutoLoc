package tn.esprit.autoloc.autolocapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Employe;
import tn.esprit.autoloc.autolocapi.dto.EmployeDTO;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeMapper {

    private final AgenceMapper agenceMapper;

    public EmployeDTO toDTO(Employe entity) {
        if (entity == null) {
            return null;
        }
        return EmployeDTO.builder()
                .idEmploye(entity.getIdEmploye())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .role(entity.getRole())
                .agence(agenceMapper.toDTO(entity.getAgence()))
                .build();
    }

    public Employe toEntity(EmployeDTO dto) {
        if (dto == null) {
            return null;
        }
        Employe entity = new Employe();
        entity.setIdEmploye(dto.getIdEmploye());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setRole(dto.getRole());
        entity.setAgence(agenceMapper.toEntity(dto.getAgence()));
        return entity;
    }

    public List<EmployeDTO> toDTOList(List<Employe> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
