package tn.esprit.autoloc.autolocapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.autolocapi.dto.ReservationDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationResponseDTO;
import tn.esprit.autoloc.autolocapi.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.autolocapi.service.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations de véhicules")
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping
    @Operation(summary = "Lister toutes les réservations", description = "Retourne la liste complète des réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès")
    public List<ReservationDTO> findAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une réservation", description = "Retourne une réservation identifiée par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    public ReservationDTO findById(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une réservation", description = "Enregistre une nouvelle réservation pour un client et un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. date de fin antérieure à date de début, identifiant présent)")
    })
    public ReservationDTO create(@Valid @RequestBody ReservationDTO reservationDTO) {
        return reservationService.create(reservationDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une réservation", description = "Met à jour les informations d'une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public ReservationDTO update(@Parameter(description = "Identifiant de la réservation à modifier") @PathVariable Long id,
                                 @Valid @RequestBody ReservationDTO reservationDTO) {
        return reservationService.update(id, reservationDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une réservation", description = "Supprime une réservation identifiée par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant de la réservation à supprimer") @PathVariable Long id) {
        reservationService.deleteById(id);
    }

    @PutMapping(value = {"/{idReservation}/client", "/{idReservation}/affecter-client", "/affecter-client/{idReservation}"})
    @Operation(summary = "Affecter une réservation à un client", description = "Associe une réservation existante à un client identifié par son nom et son prénom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation affectée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation ou client introuvable")
    })
    public ReservationResponseDTO affecterReservationAClient(
            @Parameter(description = "Identifiant de la réservation") @PathVariable Long idReservation,
            @Parameter(description = "Nom du client") @RequestParam String nom,
            @Parameter(description = "Prénom du client") @RequestParam String prenom) {
        return reservationService.affecterReservationAClient(idReservation, nom, prenom);
    }

    @PostMapping(value = {"/avec-contrat", "/creer-avec-contrat"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une réservation et son contrat associé", description = "Crée simultanément une nouvelle réservation et son contrat de location associé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation et contrat associés créés avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la réservation ou le contrat"),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable")
    })
    public ReservationResponseDTO creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {
        return reservationService.creerReservationEtContratAssocie(requestDTO);
    }
}
