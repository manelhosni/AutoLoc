package tn.esprit.autoloc.autolocapi.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Equipement;
import tn.esprit.autoloc.autolocapi.dto.EquipementDTO;

import java.util.Collections;
import java.util.List;

@Component
public class EquipementMapper {

    public EquipementDTO toDTO(Equipement entity) {
        if (entity == null) {
            return null;
        }
        return EquipementDTO.builder()
                .idEquipement(entity.getIdEquipement())
                .libelle(entity.getLibelle())
                .build();
    }

    public Equipement toEntity(EquipementDTO dto) {
        if (dto == null) {
            return null;
        }
        Equipement entity = new Equipement();
        entity.setIdEquipement(dto.getIdEquipement());
        entity.setLibelle(dto.getLibelle());
        return entity;
    }

    public List<EquipementDTO> toDTOList(List<Equipement> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
