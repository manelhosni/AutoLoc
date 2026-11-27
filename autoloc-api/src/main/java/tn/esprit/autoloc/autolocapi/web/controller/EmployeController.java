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
import tn.esprit.autoloc.autolocapi.dto.EmployeDTO;
import tn.esprit.autoloc.autolocapi.service.IEmployeService;

import java.util.List;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des employés des agences")
public class EmployeController {

    private final IEmployeService employeService;

    @GetMapping
    @Operation(summary = "Lister tous les employés", description = "Retourne la liste complète des employés")
    @ApiResponse(responseCode = "200", description = "Liste des employés récupérée avec succès")
    public List<EmployeDTO> findAll() {
        return employeService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un employé", description = "Retourne un employé identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    public EmployeDTO findById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        return employeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un employé", description = "Crée un nouvel employé rattaché à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides (ex. nom ou rôle manquant, identifiant présent)")
    })
    public EmployeDTO create(@Valid @RequestBody EmployeDTO employeDTO) {
        return employeService.create(employeDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un employé", description = "Met à jour un employé existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable"),
            @ApiResponse(responseCode = "400", description = "Données invalides pour la modification")
    })
    public EmployeDTO update(@Parameter(description = "Identifiant de l'employé à modifier") @PathVariable Long id,
                             @Valid @RequestBody EmployeDTO employeDTO) {
        return employeService.update(id, employeDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un employé", description = "Supprime un employé identifié par son ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable")
    })
    public void deleteById(@Parameter(description = "Identifiant de l'employé à supprimer") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}
