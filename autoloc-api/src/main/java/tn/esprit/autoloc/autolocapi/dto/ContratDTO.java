package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un contrat de location")
public class ContratDTO {

    @Schema(description = "Identifiant unique du contrat", example = "1")
    private Long idContrat;

    @NotNull(message = "La date de signature est obligatoire")
    @Schema(description = "Date de conclusion du contrat", example = "2026-11-02", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    @PositiveOrZero(message = "Le montant total ne peut pas être négatif")
    @Schema(description = "Montant total facturé", example = "480.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal montantTotal;

    @Schema(description = "Indique si le contrat est validé", example = "true")
    private Boolean valide;

    public boolean isValide() {
        return Boolean.TRUE.equals(valide);
    }

    @Valid
    @Schema(description = "Paiements associés au contrat")
    private Set<PaiementDTO> paiements;
}
