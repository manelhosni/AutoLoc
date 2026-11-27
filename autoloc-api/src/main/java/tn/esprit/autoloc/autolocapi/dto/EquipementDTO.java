package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un équipement optionnel")
public class EquipementDTO {

    @Schema(description = "Identifiant unique de l'équipement", example = "1")
    private Long idEquipement;

    @NotBlank(message = "Le libellé de l'équipement ne peut pas être vide")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    @Schema(description = "Désignation de l'équipement", example = "GPS intégré", requiredMode = Schema.RequiredMode.REQUIRED)
    private String libelle;
}
