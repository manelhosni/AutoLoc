package tn.esprit.autoloc.autolocapi.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.autolocapi.dto.*;
import tn.esprit.autoloc.autolocapi.service.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentControllersTest {

    @Mock
    private IReservationService reservationService;

    @Mock
    private IVehiculeService vehiculeService;

    @Mock
    private IContratService contratService;

    @InjectMocks
    private ReservationController reservationController;

    @InjectMocks
    private VehiculeController vehiculeController;

    @InjectMocks
    private ContratController contratController;

    @Test
    @DisplayName("ReservationController.affecterReservationAClient - Délègue au service sans logique métier")
    void testReservationController_affecterClient() {
        ReservationResponseDTO dto = ReservationResponseDTO.builder().idReservation(1L).build();
        when(reservationService.affecterReservationAClient(1L, "Ben Salah", "Mohamed")).thenReturn(dto);

        ReservationResponseDTO result = reservationController.affecterReservationAClient(1L, "Ben Salah", "Mohamed");

        assertSame(dto, result);
        verify(reservationService).affecterReservationAClient(1L, "Ben Salah", "Mohamed");
    }

    @Test
    @DisplayName("ReservationController.creerReservationEtContratAssocie - Délègue au service sans logique métier")
    void testReservationController_creerAvecContrat() {
        ReservationWithContratRequestDTO request = ReservationWithContratRequestDTO.builder().build();
        ReservationResponseDTO dto = ReservationResponseDTO.builder().idReservation(2L).build();
        when(reservationService.creerReservationEtContratAssocie(request)).thenReturn(dto);

        ReservationResponseDTO result = reservationController.creerReservationEtContratAssocie(request);

        assertSame(dto, result);
        verify(reservationService).creerReservationEtContratAssocie(request);
    }

    @Test
    @DisplayName("VehiculeController.affecterVehiculeAEquipement - Délègue au service sans logique métier")
    void testVehiculeController_affecterEquipement() {
        VehiculeDTO dto = VehiculeDTO.builder().idVehicule(1L).build();
        when(vehiculeService.affecterVehiculeAEquipement(1L, 2L)).thenReturn(dto);

        VehiculeDTO result = vehiculeController.affecterVehiculeAEquipement(1L, 2L);

        assertSame(dto, result);
        verify(vehiculeService).affecterVehiculeAEquipement(1L, 2L);
    }

    @Test
    @DisplayName("VehiculeController.affecterMaintenanceAvehicule - Délègue au service sans logique métier")
    void testVehiculeController_affecterMaintenance() {
        VehiculeDTO dto = VehiculeDTO.builder().immatriculation("210-TN-4567").build();
        when(vehiculeService.affecterMaintenanceAvehicule("Vidange", "210-TN-4567")).thenReturn(dto);

        VehiculeDTO result = vehiculeController.affecterMaintenanceAvehicule("Vidange", "210-TN-4567", null);

        assertSame(dto, result);
        verify(vehiculeService).affecterMaintenanceAvehicule("Vidange", "210-TN-4567");
    }

    @Test
    @DisplayName("ContratController.affecterContratAReservation - Délègue au service sans logique métier")
    void testContratController_affecterReservation() {
        LocalDate now = LocalDate.now();
        ContratDTO dto = ContratDTO.builder().idContrat(1L).build();
        when(contratService.affecterContratAReservation(now, now, now.plusDays(5))).thenReturn(dto);

        ContratDTO result = contratController.affecterContratAReservation(now, now, now.plusDays(5));

        assertSame(dto, result);
        verify(contratService).affecterContratAReservation(now, now, now.plusDays(5));
    }
}
