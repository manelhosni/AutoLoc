package tn.esprit.autoloc.autolocapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Maintenance;
import tn.esprit.autoloc.autolocapi.dto.MaintenanceDTO;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintenanceMapper {

    private final VehiculeMapper vehiculeMapper;

    public MaintenanceDTO toDTO(Maintenance entity) {
        if (entity == null) {
            return null;
        }
        return MaintenanceDTO.builder()
                .idMaintenance(entity.getIdMaintenance())
                .dateDebut(entity.getDateDebut())
                .dateFin(entity.getDateFin())
                .description(entity.getDescription())
                .vehicule(vehiculeMapper.toDTO(entity.getVehicule()))
                .build();
    }

    public Maintenance toEntity(MaintenanceDTO dto) {
        if (dto == null) {
            return null;
        }
        Maintenance entity = new Maintenance();
        entity.setIdMaintenance(dto.getIdMaintenance());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDescription(dto.getDescription());
        entity.setVehicule(vehiculeMapper.toEntity(dto.getVehicule()));
        return entity;
    }

    public List<MaintenanceDTO> toDTOList(List<Maintenance> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
