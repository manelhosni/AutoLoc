# Note de synthèse — Atelier 6 : DTOs, Mappers, Validation des données et Gestion Centralisée des Exceptions

---

## 1. Contexte et Objectifs

Cet atelier poursuit la professionnalisation de l'architecture de l'API REST **AutoLoc** en répondant à deux impératifs majeurs :
1. **Découplage de la couche d'exposition et des entités JPA** : Utilisation exclusive des DTO (*Data Transfer Objects*) en arguments et valeurs de retour des méthodes des contrôleurs REST, résolvant définitivement la violation SonarQube `java:S4684` (*Persistent entities should not be used as arguments or return values of @RequestMapping methods*).
2. **Validation robuste et Centralisation du traitement des erreurs** : Intégration de la dépendance standard `spring-boot-starter-validation`, application d'annotations de contraintes déclaratives sur les champs des DTOs, et interception uniforme des exceptions par un `@RestControllerAdvice` (`GlobalExceptionHandler`).

---

## 2. Architecture des DTOs et Mappers

### A. Répertoire des DTOs (`tn.esprit.autoloc.autolocapi.dto`)

| DTO | Rôle métier | Champs principaux | Contraintes Bean Validation appliquées |
| :--- | :--- | :--- | :--- |
| **`AgenceDTO`** | Échange d'informations d'agence | `idAgence`, `nom`, `ville`, `adresse`, `telephone` | `@NotBlank`, `@Size(max = 100)` sur `nom` |
| **`ClientDTO`** | Données d'inscription et de contact client | `idClient`, `nom`, `prenom`, `email`, `telephone`, `numPermis`, `dateInscription` | `@NotBlank` sur `nom`, `@NotBlank` et `@Email` sur `email` |
| **`PaiementDTO`** | Détail d'une transaction de règlement | `idPaiement`, `montant`, `datePaiement`, `modePaiement`, `idContrat` | `@NotNull`, `@Positive` sur `montant`, `@NotNull` sur `datePaiement`, `modePaiement` |
| **`ContratDTO`** | Contrat de location et ses règlements | `idContrat`, `dateSignature`, `montantTotal`, `valide`, `paiements` | `@NotNull`, `@PositiveOrZero` sur `montantTotal`, `@NotNull` sur `dateSignature`, `@Valid` sur `paiements` |
| **`EmployeDTO`** | Collaborateur rattaché à une agence | `idEmploye`, `nom`, `prenom`, `role`, `agence` | `@NotBlank` sur `nom`, `@NotNull` sur `role` |
| **`EquipementDTO`** | Catalogue des options de véhicule | `idEquipement`, `libelle` | `@NotBlank` sur `libelle` |
| **`VehiculeDTO`** | Fiche véhicule de la flotte | `idVehicule`, `immatriculation`, `marque`, `modele`, `tarifJournalier`, `categorie`, `statut`, `agence` | `@NotBlank` sur `immatriculation`, `marque`, `modele`, `@NotNull` et `@PositiveOrZero` sur `tarifJournalier` |
| **`MaintenanceDTO`** | Intervention mécanique ou technique | `idMaintenance`, `dateDebut`, `dateFin`, `description`, `vehicule` | `@NotNull` sur `dateDebut`, `dateFin` |
| **`ReservationDTO`** | Réservation planifiée d'un véhicule | `idReservation`, `dateDebut`, `dateFin`, `statut`, `client`, `vehicule`, `contrat` | `@NotNull` sur `dateDebut`, `dateFin` |
| **`ErrorResponseDTO`** | Format standardisé des retours d'erreur | `statut`, `status`, `message`, `horodatage`, `timestamp`, `erreurs`, `errors` | Format JSON universel |

### B. Couche Mappers (`tn.esprit.autoloc.autolocapi.mapper`)

Chaque entité dispose d'un composant Spring dédié (`@Component`) assurant la transformation bidirectionnelle sans risque de récursion cyclique :
* `toDTO(Entity)` : conversion d'une entité vers son DTO.
* `toEntity(DTO)` : reconstruction de l'entité à partir du DTO.
* `toDTOList(List<Entity>)` : transformation de collections.

Exemple de rupture de boucle entre `Contrat` et `Paiement` :
* `ContratDTO` intègre une collection de `PaiementDTO`.
* `PaiementDTO` ne référence plus l'objet `ContratDTO` entier, mais un simple identifiant scalaire `idContrat`.

---

## 3. Validation Déclarative et Gestion Centralisée des Exceptions

### A. Dépendance Maven
Ajoutée dans `pom.xml` :
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### B. Contrôles dans les Contrôleurs REST
Chaque méthode `@PostMapping` et `@PutMapping` annote le corps de requête avec `@Valid` :
```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public AgenceDTO create(@Valid @RequestBody AgenceDTO agenceDTO) {
    return agenceService.create(agenceDTO);
}
```

### C. Classe `GlobalExceptionHandler` (`@RestControllerAdvice`)

| Exception interceptée | Statut HTTP | Description du traitement | Format du corps retourné |
| :--- | :--- | :--- | :--- |
| **`MethodArgumentNotValidException`** | `400 Bad Request` | Déclenchée lors de l'échec des contraintes `@Valid` des DTOs | Capture chaque `FieldError` dans un dictionnaire `erreurs` / `errors` |
| **`ResourceNotFoundException`** | `404 Not Found` | Déclenchée lorsqu'un identifiant demandé n'existe pas en base | Message ciblé indiquant le nom de l'entité et l'ID manquant |
| **`IllegalArgumentException`** | `400 Bad Request` | Violation des règles métier (ex. date de fin antérieure, montant négatif) | Message d'anomalie métier |
| **`HttpMessageNotReadableException`** | `400 Bad Request` | Corps JSON malformé, tronqué ou valeur d'enum invalide | Alerte de syntaxe JSON |
| **`MethodArgumentTypeMismatchException`** | `400 Bad Request` | Typage incorrect dans une URL (ex. chaîne passée pour un ID numérique) | Précision du paramètre fautif |
| **`HttpRequestMethodNotSupportedException`** | `405 Method Not Allowed` | Tentative d'utilisation d'un verbe non exposé (ex. `POST /api/paiements`) | Indication de la méthode interdite |
| **`Exception`** | `500 Internal Server Error` | Erreurs imprévues non interceptées | Masquage des traces sensibles, notification propre |

---

## 4. Résultats et Traces d'Exécution des Tests

Tous les tests nominaux et de validation ont été exécutés avec succès sur l'environnement réel :

### 1. Test de validation Bean Validation (Nom d'agence vide)
* **Requête** : `POST /api/agences` avec `{"nom": "", "ville": "Sousse"}`
* **Statut** : `400 Bad Request`
* **Corps de réponse** :
```json
{
  "statut": 400,
  "status": 400,
  "message": "Erreur de validation des données transmises",
  "horodatage": "2026-11-09T20:40:16.534",
  "timestamp": "2026-11-09T20:40:16.534",
  "erreurs": {
    "nom": "Le nom de l'agence est obligatoire"
  },
  "errors": {
    "nom": "Le nom de l'agence est obligatoire"
  }
}
```

### 2. Test de validation de format d'email (Client)
* **Requête** : `POST /api/clients` avec `{"nom": "Ben Amor", "email": "format_email_invalide"}`
* **Statut** : `400 Bad Request`
* **Corps de réponse** :
```json
{
  "statut": 400,
  "status": 400,
  "message": "Erreur de validation des données transmises",
  "horodatage": "2026-11-09T20:40:16.608",
  "timestamp": "2026-11-09T20:40:16.608",
  "erreurs": {
    "email": "Format d'adresse email invalide"
  },
  "errors": {
    "email": "Format d'adresse email invalide"
  }
}
```

### 3. Test de validation du tarif journalier négatif (Véhicule)
* **Requête** : `POST /api/vehicules` avec `{"immatriculation": "250-TN-9999", "tarifJournalier": -40.0, ...}`
* **Statut** : `400 Bad Request`
* **Corps de réponse** :
```json
{
  "statut": 400,
  "status": 400,
  "message": "Erreur de validation des données transmises",
  "erreurs": {
    "tarifJournalier": "Le tarif journalier ne peut pas être négatif"
  }
}
```

### 4. Test de cascade PERSIST Contrat $\to$ Paiements
* **Requête** : `POST /api/contrats` avec 2 paiements imbriqués (`CARTE` 200 DT, `CHEQUE` 300 DT)
* **Statut** : `201 Created`
* **Consultation** : `GET /api/paiements` retourne la liste des paiements contenant `idContrat` sans cycle infini.

### 5. Test 404 Ressource introuvable
* **Requête** : `GET /api/agences/99999`
* **Statut** : `404 Not Found`
* **Corps de réponse** :
```json
{
  "statut": 404,
  "status": 404,
  "message": "Agence introuvable avec l'identifiant 99999",
  "horodatage": "2026-11-09T20:40:17.104",
  "timestamp": "2026-11-09T20:40:17.104"
}
```

### 6. Test 405 Méthode non autorisée
* **Requête** : `POST /api/paiements`
* **Statut** : `405 Method Not Allowed`
* **Corps de réponse** :
```json
{
  "statut": 405,
  "status": 405,
  "message": "Méthode HTTP 'POST' non autorisée pour cette ressource"
}
```
