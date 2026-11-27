package tn.esprit.autoloc.autolocapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Vehicule;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final AgenceMapper agenceMapper;

    public VehiculeDTO toDTO(Vehicule entity) {
        if (entity == null) {
            return null;
        }
        return VehiculeDTO.builder()
                .idVehicule(entity.getIdVehicule())
                .immatriculation(entity.getImmatriculation())
                .marque(entity.getMarque())
                .modele(entity.getModele())
                .tarifJournalier(entity.getTarifJournalier())
                .categorie(entity.getCategorie())
                .statut(entity.getStatut())
                .agence(agenceMapper.toDTO(entity.getAgence()))
                .build();
    }

    public Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) {
            return null;
        }
        Vehicule entity = new Vehicule();
        entity.setIdVehicule(dto.getIdVehicule());
        entity.setImmatriculation(dto.getImmatriculation());
        entity.setMarque(dto.getMarque());
        entity.setModele(dto.getModele());
        entity.setTarifJournalier(dto.getTarifJournalier());
        entity.setCategorie(dto.getCategorie());
        entity.setStatut(dto.getStatut());
        entity.setAgence(agenceMapper.toEntity(dto.getAgence()));
        return entity;
    }

    public List<VehiculeDTO> toDTOList(List<Vehicule> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
