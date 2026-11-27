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
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.service.IVehiculeService;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion du parc de véhicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @GetMapping
    @Operation(summary = "Lister tous les véhicules", description = "Retourne la liste complète des véhicules disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    public List<VehiculeDTO> findAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule", description = "Retourne un véhicule identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    public VehiculeDTO findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un véhicule", description = "Ajoute un nouveau véhicule au parc")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. tarif négatif, identifiant présent)")
    })
    public VehiculeDTO create(@Valid @RequestBody VehiculeDTO vehiculeDTO) {
        return vehiculeService.create(vehiculeDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule", description = "Met à jour les informations d'un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public VehiculeDTO update(@Parameter(description = "Identifiant du véhicule à modifier") @PathVariable Long id,
                              @Valid @RequestBody VehiculeDTO vehiculeDTO) {
        return vehiculeService.update(id, vehiculeDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un véhicule", description = "Supprime un véhicule identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant de la réservation à supprimer") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @PutMapping(value = {"/{idVehicule}/equipements/{idEquipement}", "/{idVehicule}/affecter-equipement/{idEquipement}", "/affecter-equipement/{idVehicule}/{idEquipement}"})
    @Operation(summary = "Affecter un équipement à un véhicule", description = "Associe un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté au véhicule avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    })
    public VehiculeDTO affecterVehiculeAEquipement(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return vehiculeService.affecterVehiculeAEquipement(idVehicule, idEquipement);
    }

    @PutMapping(value = {"/affecter-maintenance", "/{immatriculation}/maintenance", "/affecter-maintenance/{immatriculation}"})
    @Operation(summary = "Affecter une maintenance à un véhicule", description = "Associe une opération de maintenance à un véhicule via son immatriculation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance affectée au véhicule avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou maintenance introuvable")
    })
    public VehiculeDTO affecterMaintenanceAvehicule(
            @Parameter(description = "Description de la maintenance") @RequestParam String description,
            @Parameter(description = "Immatriculation du véhicule") @RequestParam(required = false) String immatriculation,
            @Parameter(description = "Immatriculation du véhicule via URL (optionnelle)") @PathVariable(name = "immatriculation", required = false) String immatriculationPath) {
        String resolvedImmat = (immatriculation != null && !immatriculation.isBlank()) ? immatriculation : immatriculationPath;
        return vehiculeService.affecterMaintenanceAvehicule(description, resolvedImmat);
    }
}
