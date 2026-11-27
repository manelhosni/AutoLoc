package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.StatutReservation;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de réponse pour les opérations sur les réservations")
public class ReservationResponseDTO {

    @Schema(description = "Identifiant unique de la réservation", example = "1")
    private Long idReservation;

    @Schema(description = "Date de début de réservation", example = "2026-11-20")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de réservation", example = "2026-11-25")
    private LocalDate dateFin;

    @Schema(description = "Statut de la réservation", example = "CONFIRMEE")
    private StatutReservation statut;

    @Schema(description = "Client ayant réservé")
    private ClientDTO client;

    @Schema(description = "Véhicule réservé")
    private VehiculeDTO vehicule;

    @Schema(description = "Contrat associé à la réservation")
    private ContratDTO contrat;
}
