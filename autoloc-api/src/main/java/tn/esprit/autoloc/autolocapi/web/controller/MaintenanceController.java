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
import tn.esprit.autoloc.autolocapi.dto.MaintenanceDTO;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.service.IMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
@Tag(name = "Maintenances", description = "Gestion des opérations de maintenance des véhicules")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @GetMapping
    @Operation(summary = "Lister toutes les maintenances", description = "Retourne la liste complète des maintenances")
    @ApiResponse(responseCode = "200", description = "Liste des maintenances récupérée avec succès")
    public List<MaintenanceDTO> findAll() {
        return maintenanceService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une maintenance", description = "Retourne une opération de maintenance identifiée par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance trouvée"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    public MaintenanceDTO findById(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une maintenance", description = "Enregistre une nouvelle opération de maintenance pour un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. date de fin antérieure à date de début, identifiant présent)")
    })
    public MaintenanceDTO create(@Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        return maintenanceService.create(maintenanceDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une maintenance", description = "Met à jour les informations d'une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public MaintenanceDTO update(@Parameter(description = "Identifiant de la maintenance à modifier") @PathVariable Long id,
                                 @Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        return maintenanceService.update(id, maintenanceDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une maintenance", description = "Supprime une maintenance identifiée par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Maintenance supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant de la maintenance à supprimer") @PathVariable Long id) {
        maintenanceService.deleteById(id);
    }

    @PutMapping(value = {"/affecter-vehicule", "/affecter-a-vehicule"})
    @Operation(summary = "Affecter une maintenance à un véhicule", description = "Associe une opération de maintenance identifiée par sa description à un véhicule identifié par son immatriculation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance affectée au véhicule avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou maintenance introuvable")
    })
    public VehiculeDTO affecterMaintenanceAvehicule(
            @Parameter(description = "Description de l'intervention de maintenance") @RequestParam String description,
            @Parameter(description = "Immatriculation du véhicule") @RequestParam String immatriculation) {
        return maintenanceService.affecterMaintenanceAvehicule(description, immatriculation);
    }
}
