package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.RoleEmploye;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un employé")
public class EmployeDTO {

    @Schema(description = "Identifiant unique de l'employé", example = "1")
    private Long idEmploye;

    @NotBlank(message = "Le nom de l'employé est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    @Schema(description = "Nom de famille de l'employé", example = "Ben Salah", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    @Schema(description = "Prénom de l'employé", example = "Ali")
    private String prenom;

    @NotNull(message = "Le rôle de l'employé est obligatoire")
    @Schema(description = "Rôle de l'employé", example = "AGENT_ACCUEIL", requiredMode = Schema.RequiredMode.REQUIRED)
    private RoleEmploye role;

    @Schema(description = "Agence à laquelle est rattaché l'employé")
    private AgenceDTO agence;
}
