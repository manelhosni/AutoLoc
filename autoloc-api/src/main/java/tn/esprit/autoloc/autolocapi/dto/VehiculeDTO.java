package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.CategorieVehicule;
import tn.esprit.autoloc.autolocapi.domain.StatutVehicule;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un véhicule du parc")
public class VehiculeDTO {

    @Schema(description = "Identifiant unique du véhicule", example = "1")
    private Long idVehicule;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 20, message = "L'immatriculation ne peut pas dépasser 20 caractères")
    @Schema(description = "Numéro d'immatriculation unique", example = "210-TN-4567", requiredMode = Schema.RequiredMode.REQUIRED)
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 50, message = "La marque ne peut pas dépasser 50 caractères")
    @Schema(description = "Marque du constructeur", example = "Renault", requiredMode = Schema.RequiredMode.REQUIRED)
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 50, message = "Le modèle ne peut pas dépasser 50 caractères")
    @Schema(description = "Modèle commercial", example = "Clio 5", requiredMode = Schema.RequiredMode.REQUIRED)
    private String modele;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @PositiveOrZero(message = "Le tarif journalier ne peut pas être négatif")
    @Schema(description = "Prix de location par jour", example = "120.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal tarifJournalier;

    @Schema(description = "Catégorie du véhicule", example = "ECONOMIQUE")
    private CategorieVehicule categorie;

    @Schema(description = "Statut opérationnel du véhicule", example = "DISPONIBLE")
    private StatutVehicule statut;

    @Schema(description = "Agence propriétaire du véhicule")
    private AgenceDTO agence;
}
