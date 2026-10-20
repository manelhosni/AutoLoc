package tn.esprit.autoloc.autolocapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.autolocapi.domain.Equipement;
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
    public List<Equipement> findAll() {
        return equipementService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un équipement", description = "Retourne un équipement identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement trouvé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable")
    })
    public Equipement findById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        return equipementService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un équipement", description = "Ajoute un nouvel équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Équipement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. libellé vide, identifiant présent)")
    })
    public Equipement create(@RequestBody Equipement equipement) {
        return equipementService.create(equipement);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un équipement", description = "Met à jour un équipement existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public Equipement update(@Parameter(description = "Identifiant de l'équipement à modifier") @PathVariable Long id, @RequestBody Equipement equipement) {
        return equipementService.update(id, equipement);
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
}
