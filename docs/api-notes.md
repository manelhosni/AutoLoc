# Note de synthèse — Atelier 5 : Spring MVC, REST, Postman et Documentation Swagger (SpringDoc OpenAPI)

---

## A. Endpoints

L'ensemble des routes est exposé sous le préfixe commun configuré via `server.servlet.context-path=/api`. Les contrôleurs définissent leurs routes relatives via `@RequestMapping` sans répéter `/api`.

| Contrôleur | Verbe & Route | Service appelé | Code succès | Codes erreur | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `ContratController` | `GET /api/contrats` | `contratService.findAll()` | `200 OK` | — | Lister tous les contrats |
| `ContratController` | `GET /api/contrats/{id}` | `contratService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un contrat par son ID |
| `ContratController` | `POST /api/contrats` | `contratService.create(contrat)` | `201 Created` | `400 Bad Request` | Créer un contrat (cascade paiements) |
| `ContratController` | `PUT /api/contrats/{id}` | `contratService.update(id, contrat)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un contrat existant |
| `ContratController` | `DELETE /api/contrats/{id}` | `contratService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un contrat (cascade paiements) |
| `PaiementController` | `GET /api/paiements` | `paiementService.findAll()` | `200 OK` | — | Lister tous les paiements (lecture seule) |
| `PaiementController` | `GET /api/paiements/{id}` | `paiementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un paiement par son ID |
| `AgenceController` | `GET /api/agences` | `agenceService.findAll()` | `200 OK` | — | Lister toutes les agences |
| `AgenceController` | `GET /api/agences/{id}` | `agenceService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une agence par son ID |
| `AgenceController` | `POST /api/agences` | `agenceService.create(agence)` | `201 Created` | `400 Bad Request` | Créer une agence (nom obligatoire) |
| `AgenceController` | `PUT /api/agences/{id}` | `agenceService.update(id, agence)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier une agence existante |
| `AgenceController` | `DELETE /api/agences/{id}` | `agenceService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une agence par son ID |
| `EmployeController` | `GET /api/employes` | `employeService.findAll()` | `200 OK` | — | Lister tous les employés |
| `EmployeController` | `GET /api/employes/{id}` | `employeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un employé par son ID |
| `EmployeController` | `POST /api/employes` | `employeService.create(employe)` | `201 Created` | `400 Bad Request` | Créer un employé (agence imbriquée) |
| `EmployeController` | `PUT /api/employes/{id}` | `employeService.update(id, employe)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un employé existant |
| `EmployeController` | `DELETE /api/employes/{id}` | `employeService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un employé par son ID |
| `VehiculeController` | `GET /api/vehicules` | `vehiculeService.findAll()` | `200 OK` | — | Lister tous les véhicules |
| `VehiculeController` | `GET /api/vehicules/{id}` | `vehiculeService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un véhicule par son ID |
| `VehiculeController` | `POST /api/vehicules` | `vehiculeService.create(vehicule)` | `201 Created` | `400 Bad Request` | Créer un véhicule (tarif journalier >= 0) |
| `VehiculeController` | `PUT /api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un véhicule existant |
| `VehiculeController` | `DELETE /api/vehicules/{id}` | `vehiculeService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un véhicule par son ID |
| `EquipementController` | `GET /api/equipements` | `equipementService.findAll()` | `200 OK` | — | Lister tous les équipements |
| `EquipementController` | `GET /api/equipements/{id}` | `equipementService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un équipement par son ID |
| `EquipementController` | `POST /api/equipements` | `equipementService.create(equipement)` | `201 Created` | `400 Bad Request` | Créer un équipement (libellé non vide) |
| `EquipementController` | `PUT /api/equipements/{id}` | `equipementService.update(id, equipement)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un équipement existant |
| `EquipementController` | `DELETE /api/equipements/{id}` | `equipementService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un équipement par son ID |
| `ClientController` | `GET /api/clients` | `clientService.findAll()` | `200 OK` | — | Lister tous les clients |
| `ClientController` | `GET /api/clients/{id}` | `clientService.findById(id)` | `200 OK` | `404 Not Found` | Consulter un client par son ID |
| `ClientController` | `POST /api/clients` | `clientService.create(client)` | `201 Created` | `400 Bad Request` | Créer un client (nom et email requis) |
| `ClientController` | `PUT /api/clients/{id}` | `clientService.update(id, client)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier un client existant |
| `ClientController` | `DELETE /api/clients/{id}` | `clientService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer un client par son ID |
| `ReservationController` | `GET /api/reservations` | `reservationService.findAll()` | `200 OK` | — | Lister toutes les réservations |
| `ReservationController` | `GET /api/reservations/{id}` | `reservationService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une réservation par son ID |
| `ReservationController` | `POST /api/reservations` | `reservationService.create(reservation)` | `201 Created` | `400 Bad Request` | Créer réservation (dateFin >= dateDebut) |
| `ReservationController` | `PUT /api/reservations/{id}` | `reservationService.update(id, reservation)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier une réservation existante |
| `ReservationController` | `DELETE /api/reservations/{id}` | `reservationService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une réservation par son ID |
| `MaintenanceController` | `GET /api/maintenances` | `maintenanceService.findAll()` | `200 OK` | — | Lister toutes les maintenances |
| `MaintenanceController` | `GET /api/maintenances/{id}` | `maintenanceService.findById(id)` | `200 OK` | `404 Not Found` | Consulter une maintenance par son ID |
| `MaintenanceController` | `POST /api/maintenances` | `maintenanceService.create(maintenance)` | `201 Created` | `400 Bad Request` | Créer maintenance (dateFin >= dateDebut) |
| `MaintenanceController` | `PUT /api/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | `200 OK` | `400 Bad Request`, `404 Not Found` | Modifier une maintenance existante |
| `MaintenanceController` | `DELETE /api/maintenances/{id}` | `maintenanceService.deleteById(id)` | `204 No Content` | `404 Not Found` | Supprimer une maintenance par son ID |

---

## B. Annotations Jackson et Gestion des Associations

### 1. Tableau des associations traitées

| Association | Entité & Attribut annoté | Annotation retenue | Justification |
| :--- | :--- | :--- | :--- |
| `Contrat` $\leftrightarrow$ `Paiement` | `Contrat.paiements` | `@JsonManagedReference("contrat-paiements")` | Composition métier (`cascade = CascadeType.ALL, orphanRemoval = true`). La liste des paiements est sérialisée au sein du JSON du contrat. |
| `Paiement` $\leftrightarrow$ `Contrat` | `Paiement.contrat` | `@JsonBackReference("contrat-paiements")` | Évite la récursion infinie lors de la sérialisation, et permet à Jackson d'injecter automatiquement l'instance du contrat parent lors de la désérialisation du corps JSON. |
| `Contrat` $\leftrightarrow$ `Reservation` | `Contrat.reservation` | `@JsonIgnore` | Évite le cycle bidirectionnel entre le contrat et sa réservation associée. |
| `Agence` $\leftrightarrow$ `Vehicule` | `Agence.vehicules` | `@JsonIgnore` | Un véhicule est créé et géré de manière autonome (`{"agence": {"idAgence": 1}}`). Évite de charger et sérialiser toute la flotte d'une agence. |
| `Agence` $\leftrightarrow$ `Employe` | `Agence.employes` | `@JsonIgnore` | L'employé référence son agence, on ignore la liste côté inverse pour éviter les boucles et alléger les réponses. |
| `Client` $\leftrightarrow$ `Reservation` | `Client.reservations` | `@JsonIgnore` | Rupture de la boucle sur le côté inverse. `Reservation.client` reste intact pour permettre l'envoi de l'identifiant client en création de réservation. |
| `Vehicule` $\leftrightarrow$ `Maintenance` | `Vehicule.maintenances` | `@JsonIgnore` | La maintenance référence son véhicule ; exclusion de la collection inverse pour prévenir la boucle infinie. |
| `Vehicule` $\leftrightarrow$ `Reservation` | `Vehicule.reservations` | `@JsonIgnore` | Exclusion de la collection inverse de réservations sur l'entité véhicule. |
| `Equipement` $\leftrightarrow$ `Vehicule` | `Equipement.vehicules` | `@JsonIgnore` | Évite la sérialisation en profondeur des véhicules liés à chaque équipement optionnel. |

### 2. Différence fondamentale entre `@JsonBackReference` et `@JsonIgnore`

* **`@JsonBackReference`** : Fait partie du binôme `@JsonManagedReference` / `@JsonBackReference`. Lors de la **sérialisation**, le champ annoté est omis pour stopper la récursion infinie. Lors de la **désérialisation**, Jackson reconstruit activement le lien relationnel : l'objet parent désérialisé est automatiquement réinjecté dans la référence annotée du fils. C'est l'outil adapté aux **compositions** (ex. `Contrat` $\to$ `Paiement`).
* **`@JsonIgnore`** : Bloque purement et simplement la sérialisation ET la désérialisation du champ. Le champ n'apparaît jamais dans le JSON sortant et est ignoré dans le JSON entrant. S'il était placé sur `Reservation.client`, Jackson refuserait de désérialiser le client passé dans le corps d'une création de réservation, laissant le champ à `null`.

---

## C. Postman et Swagger (SpringDoc OpenAPI) : Rôles et Complémentarité

| Outil | Rôle principal | Ce qu'il apporte au développeur | Exemple concret rencontré dans l'atelier |
| :--- | :--- | :--- | :--- |
| **Postman** | Client HTTP dynamique & outil de test fonctionnel | Automatisation de scénarios de tests, scripts d'assertion (`pm.test()`), persistance et partage de variables d'environnement (`pm.environment.set()`), tests de validation négative (400, 404, 405). | Création d'un contrat avec deux paiements imbriqués via un seul `POST /api/contrats`, capture de `idContrat`, puis vérification par `GET /api/paiements` que la cascade PERSIST a bien inséré les deux paiements liés en base. |
| **Swagger UI (SpringDoc)** | Documentation interactive & spécification OpenAPI | Contrat d'interface synchronisé avec le code source, exploration visuelle des modèles de données (Schemas), formulaire d'essai immédiat (*Try it out*), documentation des codes HTTP (200, 201, 204, 400, 404). | Visualisation groupée des tags `Contrats` et `Paiements`, inspection des champs obligatoires et types d'attributs, et constat immédiat sur Swagger que `PaiementController` n'expose aucun endpoint d'écriture (`POST`/`PUT`/`DELETE`). |

**Conclusion** : Postman et Swagger ne s'excluent pas mais se complètent : Swagger fournit la documentation vivante et le contrat de l'API pour les équipes consommatrices, tandis que Postman valide rigoureusement l'application des règles métier et la non-régression.

---

## D. Anomalies SonarQube for IDE Relevées et Corrigées

| Règle SonarQube | Intitulé & Problématique | Correction appliquée |
| :--- | :--- | :--- |
| `java:S1128` | *Unused imports should be removed* : Des imports superflus (ex. `ResponseEntity`, classes non utilisées) encombrent le code et alourdissent la maintenance. | Nettoyage complet des imports inutilisés dans l'ensemble des contrôleurs du package `web.controller`. |
| `java:S6813` | *Field dependency injection should be avoided* : L'injection par champ direct `@Autowired` rompt l'encapsulation et empêche les dépendances d'être `final`. | Utilisation exclusive de l'injection par constructeur via `@RequiredArgsConstructor` de Lombok et champs déclarés `private final`. |
| `java:S4684` | *Persistent entities should not be used as arguments or return values of @RequestMapping methods* : Exposer directement les entités JPA dans les contrôleurs REST crée un fort couplage avec le schéma de base de données et introduit un risque de sur-affectation (*Mass Assignment*). | Les contrôleurs ont été factorisés et allégés ; l'utilisation des DTO (*Data Transfer Objects*) au Chapitre 10 viendra résoudre définitivement cette anomalie d'architecture. |

---

## E. Questions de Compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP sont reçues par la **`DispatcherServlet`** (Front Controller de Spring MVC). Pour localiser la méthode cible, elle consulte le composant **`HandlerMapping`** (plus précisément `RequestMappingHandlerMapping`), qui compare la méthode HTTP (GET, POST, etc.) et le chemin de l'URL avec les annotations `@RequestMapping`, `@GetMapping`, etc. déclarées sur les contrôleurs. La méthode correspondante est ensuite invoquée par le `HandlerAdapter`.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
* Il ne dépend pas de `ContratRepository` car le contrôleur Web ne doit jamais court-circuiter la couche Service, garante de la logique métier, des règles de validation et des transactions.
* Il dépend de l'interface `IContratService` plutôt que de la classe concrète `ContratServiceImpl` afin de respecter le principe d'Inversion des Dépendances (DIP de SOLID) : cela découple la couche Web de l'implémentation spécifique et facilite les tests unitaires via des doublons (mocks).

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
* Selon la sémantique HTTP/REST :
  * Un `POST` créant une nouvelle ressource pérenne renvoie **`201 Created`** pour signaler que la ressource a bien été générée côté serveur.
  * Un `DELETE` aboutissant à la suppression effective ne renvoie aucun contenu dans le corps de réponse, ce qui est signifié par **`204 No Content`**.
* Une ressource absente signale une requête client portant sur un identifiant inexistant : c'est une anomalie client normale modélisée par **`404 Not Found`**. Un code **`500 Internal Server Error`** signifierait un échec interne imprévu du serveur (bug applicatif, coupure base), ce qui est sémantiquement faux lorsqu'un simple identifiant demandé n'existe pas.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
* Dans le modèle AutoLoc, un `Paiement` appartient obligatoirement à un `Contrat` (agrégat/composition). Sa création ou sa suppression est gouvernée par le contrat via la cascade JPA (`CascadeType.ALL`). Pour préserver l'intégrité financière et relationnelle, les paiements ne peuvent pas être instanciés indépendamment.
* Une requête `POST /api/paiements` reçoit une réponse HTTP **`405 Method Not Allowed`** car Spring MVC reconnaît la ressource `/paiements` mais n'a associé aucun gestionnaire pour le verbe `POST`.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
* **Risques de l'exposition directe des entités JPA** :
  1. *Fuite de données internes* : transmission non souhaitée de métadonnées techniques ou d'attributs confidentiels.
  2. *Vulnérabilité de sur-affectation (Over-posting)* : possibilité pour un client de modifier des attributs non autorisés (ex. statut, validation).
  3. *Couplage fort* : toute modification structurelle de la base de données casse les contrats clients externes.
  4. *Boucles infinies et proxys LAZY* lors de la sérialisation Jackson (`StackOverflowError` ou `ByteBuddyInterceptor` fail).
* **`Contrat.paiements` vs `Agence.vehicules`** :
  * `Contrat` $\leftrightarrow$ `Paiement` est une relation parent-enfant forte où le parent est naturellement transmis avec ses enfants (`@JsonManagedReference` / `@JsonBackReference` permet de lire et sérialiser les paiements du contrat tout en rattachant le contrat à la désérialisation).
  * `Agence` $\leftrightarrow$ `Vehicule` lie des entités au cycle de vie autonome : un véhicule est géré indépendamment et pointe vers son agence (`{"agence": {"idAgence": 1}}`). Inclure la liste des véhicules dans chaque agence alourdirait massivement les payloads et provoquerait des cycles avec les maintenances et réservations ; `@JsonIgnore` coupe net la collection inverse.
