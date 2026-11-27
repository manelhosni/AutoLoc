package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.StatutReservation;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant une réservation de véhicule")
public class ReservationDTO {

    @Schema(description = "Identifiant unique de la réservation", example = "1")
    private Long idReservation;

    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de début de réservation", example = "2026-11-05", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Schema(description = "Date de fin de réservation", example = "2026-11-10", requiredMode = Schema.RequiredMode.REQUIRED)
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
