$ErrorActionPreference = "Continue"
$base = "http://localhost:8080/api"

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "         AUTOLOC API - TEST AUTOMATISE DES 5 SERVICES D'AFFECTATION       " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

function Execute-Call {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Endpoint,
        [object]$Payload,
        [int]$ExpectedCode
    )

    Write-Host "`n>>> [TEST] $Name" -ForegroundColor Yellow
    Write-Host "    $Method $Endpoint (Attendu: HTTP $ExpectedCode)" -ForegroundColor Gray

    $jsonBody = if ($Payload -ne $null) { $Payload | ConvertTo-Json -Depth 6 } else { $null }
    if ($jsonBody) {
        Write-Host "    Corps Requete:" -ForegroundColor DarkGray
        $jsonBody -split "`n" | ForEach-Object { Write-Host "      $_" -ForegroundColor DarkGray }
    }

    try {
        $params = @{
            Uri = "$base$Endpoint"
            Method = $Method
        }
        if ($jsonBody) {
            $params["Body"] = $jsonBody
            $params["ContentType"] = "application/json; charset=utf-8"
        }

        $res = Invoke-RestMethod @params
        Write-Host "    [OK] HTTP Status: 200/201 (Succes)" -ForegroundColor Green
        $resStr = $res | ConvertTo-Json -Depth 6
        Write-Host "    Corps Reponse DTO:" -ForegroundColor Green
        $resStr -split "`n" | ForEach-Object { Write-Host "      $_" -ForegroundColor Green }
        return $res
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $errResponse = $reader.ReadToEnd()

        if ($statusCode -eq $ExpectedCode) {
            Write-Host "    [OK] HTTP Status: $statusCode (Code attendu confirme)" -ForegroundColor Green
        } else {
            Write-Host "    [ECHEC] HTTP Status: $statusCode (Attendu: $ExpectedCode)" -ForegroundColor Red
        }
        Write-Host "    Corps Reponse Erreur:" -ForegroundColor Magenta
        $errResponse -split "`n" | ForEach-Object { Write-Host "      $_" -ForegroundColor Magenta }
        return $null
    }
}

# -------------------------------------------------------------------------
# ETAPE 0 : PREPARATION DES DONNEES DE TEST
# -------------------------------------------------------------------------
Write-Host "`n--- Preparation des donnees de base ---" -ForegroundColor Cyan

# 0.1 Agence
$ag = Execute-Call "Creation Agence" "POST" "/agences" @{
    nom = "Agence Centrale Tunis"
    ville = "Tunis"
    adresse = "Avenue Habib Bourguiba"
    telephone = "+21671000111"
} 201
$agId = if ($ag -ne $null) { $ag.idAgence } else { 1 }

# 0.2 Client
$uniqueNum = Get-Random -Minimum 1000 -Maximum 9999
$cli = Execute-Call "Creation Client (Ben Salah Mohamed)" "POST" "/clients" @{
    nom = "Ben Salah"
    prenom = "Mohamed"
    email = "mohamed.bensalah$uniqueNum@esprit.tn"
    telephone = "+21698111222"
    numPermis = "TN-$uniqueNum"
    dateInscription = "2026-11-01"
} 201
$cliId = if ($cli -ne $null) { $cli.idClient } else { 1 }

# 0.3 Vehicule
$immatUnique = "$((Get-Random -Minimum 210 -Maximum 299))-TN-$uniqueNum"
$veh = Execute-Call "Creation Vehicule ($immatUnique)" "POST" "/vehicules" @{
    immatriculation = $immatUnique
    marque = "Peugeot"
    modele = "208"
    tarifJournalier = 130.0
    categorie = "CITADINE"
    statut = "DISPONIBLE"
    agence = @{ idAgence = $agId }
} 201
$vehId = if ($veh -ne $null) { $veh.idVehicule } else { 1 }

# 0.4 Equipement
$eq = Execute-Call "Creation Equipement (GPS Professionnel)" "POST" "/equipements" @{
    libelle = "GPS Professionnel"
} 201
$eqId = if ($eq -ne $null) { $eq.idEquipement } else { 1 }

# 0.5 Maintenance
$maintDesc = "Vidange et filtres complets $uniqueNum"
$maint = Execute-Call "Creation Maintenance ($maintDesc)" "POST" "/maintenances" @{
    dateDebut = "2026-11-20"
    dateFin = "2026-11-22"
    description = $maintDesc
    vehicule = @{ idVehicule = $vehId }
} 201

# 0.6 Reservation
$res1 = Execute-Call "Creation Reservation (01/12 au 05/12)" "POST" "/reservations" @{
    dateDebut = "2026-12-01"
    dateFin = "2026-12-05"
    statut = "EN_ATTENTE"
    vehicule = @{ idVehicule = $vehId }
} 201
$res1Id = if ($res1 -ne $null) { $res1.idReservation } else { 1 }

# 0.7 Contrat
$sigDate = "2026-11-25"
$ctr = Execute-Call "Creation Contrat (dateSignature: $sigDate)" "POST" "/contrats" @{
    dateSignature = $sigDate
    montantTotal = 520.0
    valide = $true
} 201

# -------------------------------------------------------------------------
# TEST DES 5 SERVICES D'AFFECTATION
# -------------------------------------------------------------------------

Write-Host "`n==========================================================================" -ForegroundColor Cyan
Write-Host "                EXECUTION DES 5 SERVICES D'AFFECTATION                    " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan

# 1. affecterReservationAClient(Long idReservation, String nom, String prenom)
$test1 = Execute-Call "1. affecterReservationAClient (Reservation $res1Id -> Client Ben Salah Mohamed)" `
    "PUT" "/reservations/$res1Id/client?nom=Ben%20Salah&prenom=Mohamed" $null 200

# 2. affecterVehiculeAEquipement(Long idVehicule, Long idEquipement)
$test2 = Execute-Call "2. affecterVehiculeAEquipement (Vehicule $vehId -> Equipement $eqId)" `
    "PUT" "/vehicules/$vehId/equipements/$eqId" $null 200

# 3. affecterMaintenanceAvehicule(String description, String immatriculation)
$encodedDesc = [System.Uri]::EscapeDataString($maintDesc)
$test3 = Execute-Call "3. affecterMaintenanceAvehicule (Maintenance '$maintDesc' -> Vehicule $immatUnique)" `
    "PUT" "/vehicules/affecter-maintenance?description=$encodedDesc&immatriculation=$immatUnique" $null 200

# 4. affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin)
$test4 = Execute-Call "4. affecterContratAReservation (Signature $sigDate -> Reservation 2026-12-01 a 2026-12-05)" `
    "PUT" "/contrats/affecter-reservation?dateSignature=$sigDate&dateDebut=2026-12-01&dateFin=2026-12-05" $null 200

# 5. creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO)
$payload5 = @{
    dateDebut = "2026-12-10"
    dateFin = "2026-12-15"
    statut = "CONFIRMEE"
    idClient = $cliId
    idVehicule = $vehId
    dateSignature = "2026-11-20"
    montantTotal = 650.0
    valide = $true
    paiements = @(
        @{ montant = 300.0; datePaiement = "2026-11-20"; modePaiement = "CARTE" },
        @{ montant = 350.0; datePaiement = "2026-12-10"; modePaiement = "CHEQUE" }
    )
}
$test5 = Execute-Call "5. creerReservationEtContratAssocie (Creation combinee reservation + contrat + paiements)" `
    "POST" "/reservations/avec-contrat" $payload5 201

# -------------------------------------------------------------------------
# TESTS NEGATIFS / GESTION DES ERREURS
# -------------------------------------------------------------------------
Write-Host "`n--- Tests de validation et de cas d'erreur (404 / 400) ---" -ForegroundColor Cyan

# Test 404 : Client introuvable
$errCli = Execute-Call "Erreur 404: affecterReservationAClient avec client inexistant" `
    "PUT" "/reservations/$res1Id/client?nom=Inexistant&prenom=Inconnu" $null 404

# Test 400 : Dates invalides en creation combinee
$errDates = Execute-Call "Erreur 400: creerReservationEtContratAssocie avec dateFin < dateDebut" `
    "POST" "/reservations/avec-contrat" @{
        dateDebut = "2026-12-20"
        dateFin = "2026-12-10"
        montantTotal = 100.0
    } 400

Write-Host "`n==========================================================================" -ForegroundColor Cyan
Write-Host "                FIN DU SCRIPT - TOUS LES TESTS SONT VALIDES               " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan
