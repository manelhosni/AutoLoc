package tn.esprit.autoloc.autolocapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Contrat;
import tn.esprit.autoloc.autolocapi.domain.Paiement;
import tn.esprit.autoloc.autolocapi.dto.ContratDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final PaiementMapper paiementMapper;

    public ContratDTO toDTO(Contrat entity) {
        if (entity == null) {
            return null;
        }
        return ContratDTO.builder()
                .idContrat(entity.getIdContrat())
                .dateSignature(entity.getDateSignature())
                .montantTotal(entity.getMontantTotal())
                .valide(entity.isValide())
                .paiements(entity.getPaiements() != null
                        ? entity.getPaiements().stream().map(paiementMapper::toDTO).collect(Collectors.toSet())
                        : Collections.emptySet())
                .build();
    }

    public Contrat toEntity(ContratDTO dto) {
        if (dto == null) {
            return null;
        }
        Contrat entity = new Contrat();
        entity.setIdContrat(dto.getIdContrat());
        entity.setDateSignature(dto.getDateSignature());
        entity.setMontantTotal(dto.getMontantTotal());
        entity.setValide(dto.isValide());

        if (dto.getPaiements() != null && !dto.getPaiements().isEmpty()) {
            Set<Paiement> paiements = new HashSet<>();
            for (var pDto : dto.getPaiements()) {
                Paiement p = paiementMapper.toEntity(pDto);
                p.setContrat(entity);
                paiements.add(p);
            }
            entity.setPaiements(paiements);
        }
        return entity;
    }

    public List<ContratDTO> toDTOList(List<Contrat> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
