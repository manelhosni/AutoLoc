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
import tn.esprit.autoloc.autolocapi.dto.EquipementDTO;
import tn.esprit.autoloc.autolocapi.dto.VehiculeDTO;
import tn.esprit.autoloc.autolocapi.service.IEquipementService;

import java.util.List;

@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
@Tag(name = "Équipements", description = "Gestion des équipements optionnels des véhicules")
public class EquipementController {

    private final IEquipementService equipementService;

    @GetMapping
    @Operation(summary = "Lister tous les équipements", description = "Retourne la liste complète des équipements disponibles")
    @ApiResponse(responseCode = "200", description = "Liste des équipements récupérée avec succès")
    public List<EquipementDTO> findAll() {
        return equipementService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un équipement", description = "Retourne un équipement identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement trouvé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    public EquipementDTO findById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        return equipementService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un équipement", description = "Ajoute un nouvel équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Équipement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. libellé vide, identifiant présent)")
    })
    public EquipementDTO create(@Valid @RequestBody EquipementDTO equipementDTO) {
        return equipementService.create(equipementDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un équipement", description = "Met à jour un équipement existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public EquipementDTO update(@Parameter(description = "Identifiant de l'équipement à modifier") @PathVariable Long id,
                                @Valid @RequestBody EquipementDTO equipementDTO) {
        return equipementService.update(id, equipementDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un équipement", description = "Supprime un équipement identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant de l'équipement à supprimer") @PathVariable Long id) {
        equipementService.deleteById(id);
    }

    @PutMapping(value = {"/{idEquipement}/vehicules/{idVehicule}", "/affecter-vehicule/{idVehicule}/{idEquipement}"})
    @Operation(summary = "Affecter un équipement à un véhicule", description = "Associe un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté au véhicule avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable")
    })
    public VehiculeDTO affecterVehiculeAEquipement(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'équipement") @PathVariable Long idEquipement) {
        return equipementService.affecterVehiculeAEquipement(idVehicule, idEquipement);
    }
}
