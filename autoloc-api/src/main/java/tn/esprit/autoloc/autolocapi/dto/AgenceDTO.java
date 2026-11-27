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
@Schema(description = "DTO représentant une agence de location")
public class AgenceDTO {

    @Schema(description = "Identifiant unique de l'agence", example = "1")
    private Long idAgence;

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Schema(description = "Nom commercial de l'agence", example = "Agence Tunis Centre", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    @Schema(description = "Ville d'implantation", example = "Tunis")
    private String ville;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    @Schema(description = "Adresse postale complète", example = "Avenue Habib Bourguiba")
    private String adresse;

    @Schema(description = "Numéro de téléphone de contact", example = "+21671000001")
    private String telephone;
}
