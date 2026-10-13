# Note de synthèse — Atelier 4 : Inversion de Contrôle, Injection de Dépendances et Couche Service

---

## A. Modes d'injection comparés

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
| :--- | :--- | :--- |
| **Champ `final` possible ?** | **Oui**, garantit l'immuabilité et la thread-safety de la dépendance | **Non**, le conteneur doit pouvoir injecter la dépendance après instanciation |
| **Dépendance visible dans la signature ?** | **Oui**, le constructeur documente explicitement les dépendances requises | **Non**, les dépendances sont masquées à l'extérieur de la classe |
| **Utilisation hors conteneur (tests avec `new`)** | **Simple et sûre**, on instancie directement avec les mocks sans framework | **Risquée**, les dépendances restent `null` à moins d'utiliser la réflexion ou Spring |
| **Signalement SonarQube for IDE / SonarLint** | Conforme aux standards Clean Code | Signalé par la règle `java:S6813` (*Field dependency injection should be avoided*) |

---

## B. Couche Service et Dépendances Injectées

Tous les services respectent la convention AutoLoc : injection par constructeur via Lombok `@RequiredArgsConstructor`, dépendances déclarées `private final` et typées par les interfaces de repositories.

| Service | Dépendances injectées | Mode d'injection | Justification |
| :--- | :--- | :--- | :--- |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur (`@RequiredArgsConstructor`) | Accès aux données agence, injection explicite et isolation modulaire. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur (`@RequiredArgsConstructor`) | Dépendance obligatoire vers l'abstraction repository pour la gestion des clients. |
| `ContratServiceImpl` | `IContratRepository` | Constructeur (`@RequiredArgsConstructor`) | Gestion métier des contrats et cascade des paiements via une dépendance immuable. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur (`@RequiredArgsConstructor`) | Validation des rôles et persistance des collaborateurs d'agence. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur (`@RequiredArgsConstructor`) | Gestion du catalogue des équipements automobiles. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur (`@RequiredArgsConstructor`) | Suivi de l'entretien des véhicules et validation des dates d'intervention. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur (`@RequiredArgsConstructor`) | Service de consultation en lecture seule avec repository immuable. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur (`@RequiredArgsConstructor`) | Validation de la cohérence temporelle et persistance des réservations. |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur (`@RequiredArgsConstructor`) | Gestion de la flotte et contrôle des tarifs journaliers. |

---

## C. Diagnostic des Messages d'Erreur du Conteneur

### Message 1
* **Erreur** : `NoSuchBeanDefinitionException` (encapsulée dans `UnsatisfiedDependencyException`).
* **Bean concerné** : `tn.esprit.autoloc.service.IContratService` (requis par `ContratController`).
* **Causes probables côté `ContratServiceImpl`** :
  1. Oubli de l'annotation `@Service` (ou `@Component`) sur la classe d'implémentation.
  2. Classe placée dans un package hors du périmètre de composant-scan de `@SpringBootApplication`.
* **Correction** : Annoter `ContratServiceImpl` avec `@Service` et vérifier son placement dans le sous-package `service`.

### Message 2
* **Erreur** : `NoUniqueBeanDefinitionException`.
* **Bean concerné** : Injection du notificateur dans `NotificationController` (deux beans trouvés : `emailNotificateur`, `smsNotificateur`).
* **Causes** : Ambiguïté de type : plusieurs beans candidats implémentent la même interface demandée.
* **Corrections possibles** :
  1. *Correction 1 (sur la classe)* : Placer `@Primary` sur la classe du bean préféré (ex. `@Primary` sur `EmailNotificateur`).
  2. *Correction 2 (à l'injection)* : Utiliser `@Qualifier("emailNotificateur")` au niveau du constructeur de `NotificationController` pour désigner explicitement le bean à injecter.

### Message 3
* **Erreur** : Dépendance circulaire entre `clientServiceImpl` et `reservationServiceImpl` (`BeanCurrentlyInCreationException`).
* **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  Pour instancier `ClientServiceImpl`, le constructeur exige une instance prête de `ReservationServiceImpl`. Mais pour créer `ReservationServiceImpl`, son constructeur réclame une instance prête de `ClientServiceImpl`. Aucun des deux beans ne peut être créé en premier, ce qui bloque le démarrage.
* **Piste de conception pour supprimer le cycle** :
  Extraire la logique commune ou partagée dans un service intermédiaire tiers (ex. `ClientReservationOrchestrator` ou `ReservationValidationService`), ou découpler les interactions en publiant des événements applicatifs (Spring Application Events).

---

## D. Anomalies SonarQube for IDE (SonarLint) Relevées et Corrigées

| Règle SonarQube | Intitulé & Explication | Correction apportée |
| :--- | :--- | :--- |
| `java:S6813` | *Field dependency injection should be avoided* : L'injection par attribut (`@Autowired`) masque les dépendances et empêche les champs d'être `final`. | Utilisation exclusive de l'injection par constructeur avec champs `final` et Lombok `@RequiredArgsConstructor`. |
| `java:S112` | *Generic exceptions should never be thrown* : Lever une exception générique (`RuntimeException`) nuit à la clarté et à la gestion ciblée des erreurs. | Création et utilisation de l'exception métier dédiée `ResourceNotFoundException`. |
| `java:S1128` | *Unused imports should be removed* : Les imports superflus alourdissent le code et les temps d'analyse. | Nettoyage des imports inutilisés dans l'ensemble des couches `service`, `exception` et `web.controller`. |

---

## E. Questions de Compréhension

1. **Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**  
   Il n'y a aucun `new ContratServiceImpl` écrit dans le code source applicatif. C'est le conteneur Spring IoC (`ApplicationContext`) qui instancie la classe par réflexion Java lors de la phase d'initialisation des beans au démarrage de l'application.

2. **Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**  
   Pour respecter le principe d'Inversion des Dépendances (DIP) des principes SOLID et assurer un faible couplage. Le contrôleur s'appuie sur un contrat (l'interface), ce qui permet de substituer librement l'implémentation (mocks de tests, implémentation décorée ou alternative) sans toucher au contrôleur.

3. **Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.**  
   Un bean singleton est unique et partagé par tous les threads exécutant simultanément les requêtes HTTP. S'il contient un état mutable propre à une requête, des accès concurrents créeront des conflits (race conditions).  
   *Exemple de bug* : Si le service conserve un attribut `private Long idContratEnCours;`, une requête de l'utilisateur A traitant le contrat 5 écrasera la variable d'une requête simultanée de l'utilisateur B traitant le contrat 9, menant à des actions appliquées sur le mauvais contrat.

4. **Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**  
   Passer directement l'objet reçu écraserait toutes les propriétés non spécifiées par le client (champs à null) et romprait les collections associées en cascade (par exemple avec `orphanRemoval=true`, vider les paiements). Charger l'existant permet de vérifier son existence, de contrôler les règles métier et de ne recopier que les champs modifiables sans toucher à l'identifiant ni aux relations gérées.

5. **Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**  
   - **`@Component` vs `@Bean`** : `@Component` s'applique sur une classe pour être auto-détectée par le scan de composants. `@Bean` s'applique sur une méthode d'une classe `@Configuration` pour déclarer explicitement une instance gérée par le conteneur (indispensable pour les classes de bibliothèques tierces non annotables).
   - **`@Primary` vs `@Qualifier`** : `@Primary` définit le bean candidat par défaut sur une classe en cas de pluralité. `@Qualifier` s'utilise au point d'injection pour désigner expressément le bean à injecter par son nom.
   - **`IPaiementService` en lecture seule** : Un paiement est indissociable d'un `Contrat` (agrégat). Sa création, sa mise à jour ou sa suppression doivent obligatoirement être pilotées par le cycle de vie du contrat afin de garantir la cohérence des montants et la gestion en cascade (`CascadeType.ALL`).
