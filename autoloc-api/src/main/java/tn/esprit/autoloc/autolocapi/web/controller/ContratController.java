package tn.esprit.autoloc.autolocapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.autolocapi.dto.ContratDTO;
import tn.esprit.autoloc.autolocapi.service.IContratService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Gestion des contrats de location")
public class ContratController {

    private final IContratService contratService;

    @GetMapping
    @Operation(summary = "Lister tous les contrats", description = "Retourne la liste complète des contrats de location")
    @ApiResponse(responseCode = "200", description = "Liste des contrats récupérée avec succès")
    public List<ContratDTO> findAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un contrat", description = "Retourne un contrat identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public ContratDTO findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un contrat", description = "Crée un nouveau contrat de location (avec paiements éventuels en cascade)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la création du contrat")
    })
    public ContratDTO create(@Valid @RequestBody ContratDTO contratDTO) {
        return contratService.create(contratDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un contrat", description = "Met à jour un contrat existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification du contrat")
    })
    public ContratDTO update(@Parameter(description = "Identifiant du contrat à modifier") @PathVariable Long id,
                             @Valid @RequestBody ContratDTO contratDTO) {
        return contratService.update(id, contratDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un contrat", description = "Supprime un contrat et ses paiements associés en cascade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant du contrat à supprimer") @PathVariable Long id) {
        contratService.deleteById(id);
    }

    @PutMapping(value = {"/affecter-reservation", "/affecter-a-reservation"})
    @Operation(summary = "Affecter un contrat à une réservation", description = "Associe un contrat repéré par sa date de signature à une réservation repérée par ses dates de début et fin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat affecté à la réservation avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat ou réservation introuvable")
    })
    public ContratDTO affecterContratAReservation(
            @Parameter(description = "Date de signature du contrat") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSignature,
            @Parameter(description = "Date de début de réservation") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin de réservation") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);
    }
}
