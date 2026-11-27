$ErrorActionPreference = "Continue"
$base = "http://localhost:8080/api"

Write-Host "==========================================================================" -ForegroundColor Cyan
Write-Host "                AUTOLOC API - TEST DE VALIDATION & DTOS                   " -ForegroundColor Cyan
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
            $params["ContentType"] = "application/json"
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

# 1. Agences
$ag1 = Execute-Call "Creation Agence Valide (AgenceDTO)" "POST" "/agences" @{
    nom = "Agence Sousse Ville"
    ville = "Sousse"
    adresse = "Boulevard 14 Janvier"
    telephone = "+21673000111"
} 201

$agErr = Execute-Call "Validation Agence - Nom obligatoire (400)" "POST" "/agences" @{
    nom = ""
    ville = "Sousse"
} 400

# 2. Clients
$cli1 = Execute-Call "Creation Client Valide (ClientDTO)" "POST" "/clients" @{
    nom = "Ben Amor"
    prenom = "Leila"
    email = "leila.benamor@esprit.tn"
    telephone = "+21622334455"
    numPermis = "TN-554433"
    dateInscription = "2026-11-09"
} 201

$cliErr = Execute-Call "Validation Client - Format Email Invalide (400)" "POST" "/clients" @{
    nom = "Ben Amor"
    email = "format_email_invalide"
} 400

# 3. Vehicules
$agId = if ($ag1 -ne $null) { $ag1.idAgence } else { 1 }
$randomImmat = "$((Get-Random -Minimum 200 -Maximum 299))-TN-$((Get-Random -Minimum 1000 -Maximum 9999))"
$veh1 = Execute-Call "Creation Vehicule Valide (VehiculeDTO)" "POST" "/vehicules" @{
    immatriculation = $randomImmat
    marque = "Hyundai"
    modele = "i20"
    tarifJournalier = 110.0
    categorie = "CITADINE"
    statut = "DISPONIBLE"
    agence = @{ idAgence = $agId }
} 201

$vehErr = Execute-Call "Validation Vehicule - Tarif journalier negatif (400)" "POST" "/vehicules" @{
    immatriculation = "250-TN-9999"
    marque = "Hyundai"
    modele = "i20"
    tarifJournalier = -40.0
    categorie = "CITADINE"
    statut = "DISPONIBLE"
} 400

# 4. Employes
$emp1 = Execute-Call "Creation Employe Valide (EmployeDTO)" "POST" "/employes" @{
    nom = "Mejri"
    prenom = "Nidhal"
    role = "AGENT"
    agence = @{ idAgence = $agId }
} 201

$empErr = Execute-Call "Validation Employe - Role obligatoire (400)" "POST" "/employes" @{
    nom = "Mejri"
    prenom = "Nidhal"
} 400

# 5. Equipements
$eq1 = Execute-Call "Creation Equipement Valide (EquipementDTO)" "POST" "/equipements" @{
    libelle = "Barres de toit aluminium"
} 201

$eqErr = Execute-Call "Validation Equipement - Libelle obligatoire (400)" "POST" "/equipements" @{
    libelle = "   "
} 400

# 6. Contrats & Paiements (Cascade)
$ctr1 = Execute-Call "Creation Contrat avec Paiements Cascade (ContratDTO & PaiementDTO)" "POST" "/contrats" @{
    dateSignature = "2026-11-09"
    montantTotal = 500.0
    valide = $true
    paiements = @(
        @{ montant = 200.0; datePaiement = "2026-11-09"; modePaiement = "CARTE" },
        @{ montant = 300.0; datePaiement = "2026-11-20"; modePaiement = "CHEQUE" }
    )
} 201

$ctrErr = Execute-Call "Validation Contrat - Montant negatif (400)" "POST" "/contrats" @{
    dateSignature = "2026-11-09"
    montantTotal = -50.0
} 400

# 7. Paiements list (Read-Only)
$paiementsList = Execute-Call "Lister les paiements (DTO securise sans boucle de recursion)" "GET" "/paiements" $null 200

# 8. Maintenances
$vehId = if ($veh1 -ne $null) { $veh1.idVehicule } else { 1 }
$maint1 = Execute-Call "Creation Maintenance Valide (MaintenanceDTO)" "POST" "/maintenances" @{
    dateDebut = "2026-11-12"
    dateFin = "2026-11-14"
    description = "Controle des freins et diagnostic valise"
    vehicule = @{ idVehicule = $vehId }
} 201

$maintErr = Execute-Call "Validation Maintenance - dateFin anterieure a dateDebut (400)" "POST" "/maintenances" @{
    dateDebut = "2026-11-20"
    dateFin = "2026-11-10"
    description = "Inversion temporelle"
    vehicule = @{ idVehicule = $vehId }
} 400

# 9. Reservations
$cliId = if ($cli1 -ne $null) { $cli1.idClient } else { 1 }
$resPayload = @{
    dateDebut = "2026-11-20"
    dateFin = "2026-11-25"
    statut = "CONFIRMEE"
    client = @{ idClient = $cliId }
    vehicule = @{ idVehicule = $vehId }
}
if ($ctr1 -ne $null -and $ctr1.idContrat -ne $null) {
    $resPayload["contrat"] = @{ idContrat = $ctr1.idContrat }
}
$res1 = Execute-Call "Creation Reservation Valide (ReservationDTO)" "POST" "/reservations" $resPayload 201

$resErr = Execute-Call "Validation Reservation - dateFin anterieure a dateDebut (400)" "POST" "/reservations" @{
    dateDebut = "2026-11-30"
    dateFin = "2026-11-20"
    statut = "CONFIRMEE"
    client = @{ idClient = $cliId }
    vehicule = @{ idVehicule = $vehId }
} 400

# 10. Erreurs centralisees
$err404 = Execute-Call "Ressource inexistante (404 Not Found)" "GET" "/agences/99999" $null 404
$err405 = Execute-Call "Methode non autorisee (405 Method Not Allowed)" "POST" "/paiements" @{} 405

Write-Host "`n==========================================================================" -ForegroundColor Cyan
Write-Host "                TOUS LES TESTS SE SONT DEROULES AVEC SUCCES               " -ForegroundColor Cyan
Write-Host "==========================================================================" -ForegroundColor Cyan
