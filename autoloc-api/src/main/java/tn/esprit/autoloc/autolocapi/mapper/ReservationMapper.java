package tn.esprit.autoloc.autolocapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.autolocapi.domain.Reservation;
import tn.esprit.autoloc.autolocapi.dto.ReservationDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationResponseDTO;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final ClientMapper clientMapper;
    private final VehiculeMapper vehiculeMapper;
    private final ContratMapper contratMapper;

    public ReservationDTO toDTO(Reservation entity) {
        if (entity == null) {
            return null;
        }
        return ReservationDTO.builder()
                .idReservation(entity.getIdReservation())
                .dateDebut(entity.getDateDebut())
                .dateFin(entity.getDateFin())
                .statut(entity.getStatut())
                .client(clientMapper.toDTO(entity.getClient()))
                .vehicule(vehiculeMapper.toDTO(entity.getVehicule()))
                .contrat(contratMapper.toDTO(entity.getContrat()))
                .build();
    }

    public ReservationResponseDTO toResponseDTO(Reservation entity) {
        if (entity == null) {
            return null;
        }
        return ReservationResponseDTO.builder()
                .idReservation(entity.getIdReservation())
                .dateDebut(entity.getDateDebut())
                .dateFin(entity.getDateFin())
                .statut(entity.getStatut())
                .client(clientMapper.toDTO(entity.getClient()))
                .vehicule(vehiculeMapper.toDTO(entity.getVehicule()))
                .contrat(contratMapper.toDTO(entity.getContrat()))
                .build();
    }

    public List<ReservationResponseDTO> toResponseDTOList(List<Reservation> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toResponseDTO).toList();
    }

    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation entity = new Reservation();
        entity.setIdReservation(dto.getIdReservation());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setStatut(dto.getStatut());
        entity.setClient(clientMapper.toEntity(dto.getClient()));
        entity.setVehicule(vehiculeMapper.toEntity(dto.getVehicule()));
        entity.setContrat(contratMapper.toEntity(dto.getContrat()));
        return entity;
    }

    public List<ReservationDTO> toDTOList(List<Reservation> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toDTO).toList();
    }
}
