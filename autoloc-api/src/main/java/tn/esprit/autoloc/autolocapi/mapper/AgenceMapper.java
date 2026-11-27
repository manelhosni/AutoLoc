package tn.esprit.autoloc.autolocapi.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Agence;
import tn.esprit.autoloc.autolocapi.dto.AgenceDTO;

import java.util.Collections;
import java.util.List;

@Component
public class AgenceMapper {

    public AgenceDTO toDTO(Agence entity) {
        if (entity == null) {
            return null;
        }
        return AgenceDTO.builder()
                .idAgence(entity.getIdAgence())
                .nom(entity.getNom())
                .ville(entity.getVille())
                .adresse(entity.getAdresse())
                .telephone(entity.getTelephone())
                .build();
    }

    public Agence toEntity(AgenceDTO dto) {
        if (dto == null) {
            return null;
        }
        Agence entity = new Agence();
        entity.setIdAgence(dto.getIdAgence());
        entity.setNom(dto.getNom());
        entity.setVille(dto.getVille());
        entity.setAdresse(dto.getAdresse());
        entity.setTelephone(dto.getTelephone());
        return entity;
    }

    public List<AgenceDTO> toDTOList(List<Agence> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
