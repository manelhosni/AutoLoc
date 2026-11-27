package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.StatutReservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de requête pour la création combinée d'une réservation et de son contrat associé")
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de début de réservation est obligatoire")
    @Schema(description = "Date de début de réservation", example = "2026-12-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin de réservation est obligatoire")
    @Schema(description = "Date de fin de réservation", example = "2026-12-05", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateFin;

    @Schema(description = "Statut de la réservation", example = "CONFIRMEE")
    private StatutReservation statut;

    @Schema(description = "Identifiant du client associé (optionnel si objet client renseigné)", example = "1")
    private Long idClient;

    @Schema(description = "Données du client associé (optionnel)")
    private ClientDTO client;

    @Schema(description = "Identifiant du véhicule associé (optionnel si objet vehicule renseigné)", example = "1")
    private Long idVehicule;

    @Schema(description = "Données du véhicule associé (optionnel)")
    private VehiculeDTO vehicule;

    // Informations du contrat associé
    @Schema(description = "Date de conclusion / signature du contrat", example = "2026-11-20")
    private LocalDate dateSignature;

    @PositiveOrZero(message = "Le montant total ne peut pas être négatif")
    @Schema(description = "Montant total facturé pour la location", example = "450.00")
    private BigDecimal montantTotal;

    @Schema(description = "Indique si le contrat est validé", example = "true")
    private Boolean valide;

    @Schema(description = "Paiements associés au contrat")
    private Set<PaiementDTO> paiements;

    @Schema(description = "Contrat associé sous forme d'objet imbriqué (optionnel)")
    private ContratDTO contrat;
}
