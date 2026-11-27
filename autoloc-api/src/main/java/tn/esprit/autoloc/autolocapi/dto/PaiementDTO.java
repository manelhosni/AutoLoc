package tn.esprit.autoloc.autolocapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import tn.esprit.autoloc.autolocapi.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO représentant un paiement")
public class PaiementDTO {

    @Schema(description = "Identifiant unique du paiement", example = "1")
    private Long idPaiement;

    @NotNull(message = "Le montant du paiement est obligatoire")
    @Positive(message = "Le montant du paiement doit être supérieur à zéro")
    @Schema(description = "Montant réglé", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    @Schema(description = "Date à laquelle le paiement a été effectué", example = "2026-11-02", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    @Schema(description = "Mode de paiement utilisé", example = "CARTE", requiredMode = Schema.RequiredMode.REQUIRED)
    private ModePaiement modePaiement;

    @Schema(description = "Identifiant du contrat rattaché", example = "1")
    private Long idContrat;
}
