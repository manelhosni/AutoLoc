package tn.esprit.autoloc.autolocapi.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Paiement;
import tn.esprit.autoloc.autolocapi.dto.PaiementDTO;

import java.util.Collections;
import java.util.List;

@Component
public class PaiementMapper {

    public PaiementDTO toDTO(Paiement entity) {
        if (entity == null) {
            return null;
        }
        return PaiementDTO.builder()
                .idPaiement(entity.getIdPaiement())
                .montant(entity.getMontant())
                .datePaiement(entity.getDatePaiement())
                .modePaiement(entity.getModePaiement())
                .idContrat(entity.getContrat() != null ? entity.getContrat().getIdContrat() : null)
                .build();
    }

    public Paiement toEntity(PaiementDTO dto) {
        if (dto == null) {
            return null;
        }
        Paiement entity = new Paiement();
        entity.setIdPaiement(dto.getIdPaiement());
        entity.setMontant(dto.getMontant());
        entity.setDatePaiement(dto.getDatePaiement());
        entity.setModePaiement(dto.getModePaiement());
        return entity;
    }

    public List<PaiementDTO> toDTOList(List<Paiement> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
