package tn.esprit.autoloc.autolocapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.autolocapi.domain.*;
import tn.esprit.autoloc.autolocapi.dto.*;
import tn.esprit.autoloc.autolocapi.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autolocapi.mapper.*;
import tn.esprit.autoloc.autolocapi.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServicesTest {

    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private IClientRepository clientRepository;
    @Mock
    private IVehiculeRepository vehiculeRepository;
    @Mock
    private IContratRepository contratRepository;
    @Mock
    private IEquipementRepository equipementRepository;
    @Mock
    private IMaintenanceRepository maintenanceRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private ContratMapper contratMapper;
    @Mock
    private VehiculeMapper vehiculeMapper;
    @Mock
    private PaiementMapper paiementMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @InjectMocks
    private VehiculeServiceImpl vehiculeService;

    @InjectMocks
    private ContratServiceImpl contratService;

    // =========================================================================
    // 1. affecterReservationAClient
    // =========================================================================
    @Test
    @DisplayName("affecterReservationAClient - Succès")
    void testAffecterReservationAClient_Success() {
        Long idRes = 10L;
        String nom = "Ben Salah";
        String prenom = "Mohamed";

        Reservation reservation = new Reservation();
        reservation.setIdReservation(idRes);

        Client client = new Client();
        client.setIdClient(1L);
        client.setNom(nom);
        client.setPrenom(prenom);

        ReservationResponseDTO expectedResponse = ReservationResponseDTO.builder()
                .idReservation(idRes)
                .client(ClientDTO.builder().nom(nom).prenom(prenom).build())
                .build();

        when(reservationRepository.findById(idRes)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomAndPrenom(nom, prenom)).thenReturn(Optional.of(client));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(expectedResponse);

        ReservationResponseDTO result = reservationService.affecterReservationAClient(idRes, nom, prenom);

        assertNotNull(result);
        assertEquals(nom, result.getClient().getNom());
        assertEquals(prenom, result.getClient().getPrenom());
        assertEquals(client, reservation.getClient());
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("affecterReservationAClient - Réservation introuvable (404)")
    void testAffecterReservationAClient_ReservationNotFound() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                reservationService.affecterReservationAClient(99L, "Nom", "Prenom"));
    }

    @Test
    @DisplayName("affecterReservationAClient - Client introuvable (404)")
    void testAffecterReservationAClient_ClientNotFound() {
        Reservation reservation = new Reservation();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomAndPrenom("Inconnu", "Inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                reservationService.affecterReservationAClient(1L, "Inconnu", "Inconnu"));
    }

    // =========================================================================
    // 2. affecterVehiculeAEquipement
    // =========================================================================
    @Test
    @DisplayName("affecterVehiculeAEquipement - Succès")
    void testAffecterVehiculeAEquipement_Success() {
        Long idVehicule = 1L;
        Long idEquipement = 2L;

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(idVehicule);
        vehicule.setImmatriculation("210-TN-4567");
        vehicule.setEquipements(new HashSet<>());

        Equipement equipement = new Equipement();
        equipement.setIdEquipement(idEquipement);
        equipement.setLibelle("GPS Garmin");
        equipement.setVehicules(new HashSet<>());

        VehiculeDTO expectedDTO = VehiculeDTO.builder()
                .idVehicule(idVehicule)
                .immatriculation("210-TN-4567")
                .build();

        when(vehiculeRepository.findById(idVehicule)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(idEquipement)).thenReturn(Optional.of(equipement));
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(expectedDTO);

        VehiculeDTO result = vehiculeService.affecterVehiculeAEquipement(idVehicule, idEquipement);

        assertNotNull(result);
        assertTrue(equipement.getVehicules().contains(vehicule));
        assertTrue(vehicule.getEquipements().contains(equipement));
        verify(equipementRepository).save(equipement);
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    @DisplayName("affecterVehiculeAEquipement - Véhicule introuvable")
    void testAffecterVehiculeAEquipement_VehiculeNotFound() {
        when(vehiculeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                vehiculeService.affecterVehiculeAEquipement(99L, 1L));
    }

    // =========================================================================
    // 3. affecterMaintenanceAvehicule
    // =========================================================================
    @Test
    @DisplayName("affecterMaintenanceAvehicule - Succès avec maintenance existante")
    void testAffecterMaintenanceAvehicule_Success() {
        String description = "Changement disques et plaquettes";
        String immatriculation = "210-TN-4567";

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMaintenances(new HashSet<>());

        Maintenance maintenance = new Maintenance();
        maintenance.setIdMaintenance(5L);
        maintenance.setDescription(description);

        VehiculeDTO expectedDTO = VehiculeDTO.builder()
                .immatriculation(immatriculation)
                .build();

        when(vehiculeRepository.findByImmatriculation(immatriculation)).thenReturn(Optional.of(vehicule));
        when(maintenanceRepository.findFirstByDescriptionIgnoreCase(description)).thenReturn(Optional.of(maintenance));
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(expectedDTO);

        VehiculeDTO result = vehiculeService.affecterMaintenanceAvehicule(description, immatriculation);

        assertNotNull(result);
        assertEquals(vehicule, maintenance.getVehicule());
        assertTrue(vehicule.getMaintenances().contains(maintenance));
        verify(maintenanceRepository).save(maintenance);
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    @DisplayName("affecterMaintenanceAvehicule - Auto-création de maintenance si inexistante")
    void testAffecterMaintenanceAvehicule_AutoCreate() {
        String description = "Controle des freins";
        String immatriculation = "210-TN-4567";

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(1L);
        vehicule.setImmatriculation(immatriculation);
        vehicule.setMaintenances(new HashSet<>());

        VehiculeDTO expectedDTO = VehiculeDTO.builder()
                .immatriculation(immatriculation)
                .build();

        when(vehiculeRepository.findByImmatriculation(immatriculation)).thenReturn(Optional.of(vehicule));
        when(maintenanceRepository.findFirstByDescriptionIgnoreCase(description)).thenReturn(Optional.empty());
        when(maintenanceRepository.findFirstByDescription(description)).thenReturn(Optional.empty());
        when(vehiculeRepository.save(any(Vehicule.class))).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(expectedDTO);

        VehiculeDTO result = vehiculeService.affecterMaintenanceAvehicule(description, immatriculation);

        assertNotNull(result);
        verify(maintenanceRepository).save(any(Maintenance.class));
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    @DisplayName("affecterMaintenanceAvehicule - Véhicule introuvable")
    void testAffecterMaintenanceAvehicule_VehiculeNotFound() {
        when(vehiculeRepository.findByImmatriculation("INVALIDE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                vehiculeService.affecterMaintenanceAvehicule("Vidange", "INVALIDE"));
    }

    // =========================================================================
    // 4. affecterContratAReservation
    // =========================================================================
    @Test
    @DisplayName("affecterContratAReservation - Succès")
    void testAffecterContratAReservation_Success() {
        LocalDate dateSignature = LocalDate.of(2026, 11, 10);
        LocalDate dateDebut = LocalDate.of(2026, 11, 15);
        LocalDate dateFin = LocalDate.of(2026, 11, 20);

        Contrat contrat = new Contrat();
        contrat.setIdContrat(1L);
        contrat.setDateSignature(dateSignature);

        Reservation reservation = new Reservation();
        reservation.setIdReservation(2L);
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);

        ContratDTO expectedDTO = ContratDTO.builder()
                .idContrat(1L)
                .dateSignature(dateSignature)
                .build();

        when(contratRepository.findFirstByDateSignature(dateSignature)).thenReturn(Optional.of(contrat));
        when(reservationRepository.findFirstByDateDebutAndDateFin(dateDebut, dateFin)).thenReturn(Optional.of(reservation));
        when(contratRepository.save(any(Contrat.class))).thenReturn(contrat);
        when(contratMapper.toDTO(contrat)).thenReturn(expectedDTO);

        ContratDTO result = contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);

        assertNotNull(result);
        assertEquals(contrat, reservation.getContrat());
        assertEquals(reservation, contrat.getReservation());
        verify(reservationRepository).save(reservation);
        verify(contratRepository).save(contrat);
    }

    @Test
    @DisplayName("affecterContratAReservation - Contrat introuvable")
    void testAffecterContratAReservation_ContratNotFound() {
        LocalDate dateSignature = LocalDate.of(2026, 1, 1);
        when(contratRepository.findFirstByDateSignature(dateSignature)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                contratService.affecterContratAReservation(dateSignature, LocalDate.now(), LocalDate.now().plusDays(2)));
    }

    // =========================================================================
    // 5. creerReservationEtContratAssocie
    // =========================================================================
    @Test
    @DisplayName("creerReservationEtContratAssocie - Succès avec DTO imbriqué")
    void testCreerReservationEtContratAssocie_Success() {
        LocalDate dateDebut = LocalDate.of(2026, 12, 1);
        LocalDate dateFin = LocalDate.of(2026, 12, 5);
        LocalDate dateSignature = LocalDate.of(2026, 11, 20);
        BigDecimal montant = BigDecimal.valueOf(450.0);

        ReservationWithContratRequestDTO requestDTO = ReservationWithContratRequestDTO.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .statut(StatutReservation.CONFIRMEE)
                .idClient(1L)
                .idVehicule(2L)
                .dateSignature(dateSignature)
                .montantTotal(montant)
                .valide(true)
                .build();

        Client client = new Client();
        client.setIdClient(1L);
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(2L);

        Contrat savedContrat = new Contrat();
        savedContrat.setIdContrat(10L);
        savedContrat.setDateSignature(dateSignature);
        savedContrat.setMontantTotal(montant);

        Reservation savedReservation = new Reservation();
        savedReservation.setIdReservation(20L);
        savedReservation.setDateDebut(dateDebut);
        savedReservation.setDateFin(dateFin);
        savedReservation.setClient(client);
        savedReservation.setVehicule(vehicule);
        savedReservation.setContrat(savedContrat);

        ReservationResponseDTO expectedResponse = ReservationResponseDTO.builder()
                .idReservation(20L)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .contrat(ContratDTO.builder().idContrat(10L).montantTotal(montant).build())
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));
        when(contratRepository.save(any(Contrat.class))).thenReturn(savedContrat);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(reservationMapper.toResponseDTO(savedReservation)).thenReturn(expectedResponse);

        ReservationResponseDTO result = reservationService.creerReservationEtContratAssocie(requestDTO);

        assertNotNull(result);
        assertEquals(20L, result.getIdReservation());
        assertEquals(10L, result.getContrat().getIdContrat());
        verify(contratRepository).save(any(Contrat.class));
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    @DisplayName("creerReservationEtContratAssocie - Date fin antérieure à date début")
    void testCreerReservationEtContratAssocie_InvalidDates() {
        ReservationWithContratRequestDTO requestDTO = ReservationWithContratRequestDTO.builder()
                .dateDebut(LocalDate.of(2026, 12, 10))
                .dateFin(LocalDate.of(2026, 12, 5))
                .build();

        assertThrows(IllegalArgumentException.class, () ->
                reservationService.creerReservationEtContratAssocie(requestDTO));
    }
}
