package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.AdminDashboardSummaryDTO;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.OilChangeRepository;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final VehicleRepository vehicleRepository;
    private final DepartureLogRepository departureLogRepository;
    private final OilChangeRepository oilChangeRepository;
    private final UserRepository userRepository;

    public AdminDashboardSummaryDTO getSummary() {

        long totalVehicles     = vehicleRepository.countByActiveTrue();
        long availableVehicles = vehicleRepository.countByActiveTrueAndAvailableTrue();
        long vehiclesInUse     = vehicleRepository.countByActiveTrueAndAvailableFalse();
        long openDepartures    = departureLogRepository.countByStatus("in_progress");
        long oilChangeAlerts   = vehicleRepository.countByActiveTrueAndOilChangeAlertSentTrue();
        long activeTechnicians = userRepository.countByActiveEmployeeTrueAndUserType("technician");
        long notTranscribed    = departureLogRepository
                .countBySgiTranscribedFalseAndStatus("completed");

        return new AdminDashboardSummaryDTO(
                totalVehicles,
                availableVehicles,
                vehiclesInUse,
                openDepartures,
                oilChangeAlerts,
                activeTechnicians,
                notTranscribed
        );
    }
}