package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant une intervention de maintenance")
public class MaintenanceDTO {

    @Schema(description = "Identifiant unique de la maintenance", example = "1")
    private Long idMaintenance;

    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de début de l'opération", example = "2026-11-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Schema(description = "Date prévue ou effective de fin d'opération", example = "2026-11-03", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateFin;

    @Schema(description = "Description des réparations ou entretiens effectués", example = "Vidange et changement plaquettes de frein")
    private String description;

    @Schema(description = "Véhicule concerné par la maintenance")
    private VehiculeDTO vehicule;
}
