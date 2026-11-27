package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un client")
public class ClientDTO {

    @Schema(description = "Identifiant unique du client", example = "1")
    private Long idClient;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    @Schema(description = "Nom de famille", example = "Trabelsi", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    @Schema(description = "Prénom du client", example = "Sami")
    private String prenom;

    @NotBlank(message = "L'email du client est obligatoire")
    @Email(message = "Format d'adresse email invalide")
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    @Schema(description = "Adresse email professionnelle ou personnelle", example = "sami.trabelsi@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+21698123456")
    private String telephone;

    @Schema(description = "Numéro du permis de conduire", example = "TN-123456")
    private String numPermis;

    @Schema(description = "Date d'inscription du client", example = "2026-10-01")
    private LocalDate dateInscription;
}
