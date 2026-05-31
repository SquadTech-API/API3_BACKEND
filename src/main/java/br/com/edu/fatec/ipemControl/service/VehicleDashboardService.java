package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleDashboardService {

    private final DepartureLogRepository departureLogRepository;
    private final FuelingRepository fuelingRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleDashboardResponseDTO findDashboard() {

        List<Object[]> results = departureLogRepository.findTop5WeeklyKilometers();

        List<KilometerChartItemDTO> weeklyTopVehicles = results.stream()
                .map(obj -> new KilometerChartItemDTO(
                        ((Number) obj[0]).intValue(),
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue()
                ))
                .toList();

        if (weeklyTopVehicles.isEmpty())
            return new VehicleDashboardResponseDTO(Map.of("week", new ArrayList<>()), null);

        Integer defaultVehicleId = weeklyTopVehicles.get(0).getId();
        VehicleDashboardDTO defaultVehicle = buildVehicleDashboard(defaultVehicleId);

        return new VehicleDashboardResponseDTO(
                Map.of("week", weeklyTopVehicles),
                defaultVehicle
        );
    }

    private VehicleDashboardDTO buildVehicleDashboard(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow();

        Double spending  = fuelingRepository.totalWeeklySpending(vehicleId);
        Double liters    = fuelingRepository.totalWeeklyLiters(vehicleId);
        Double km        = departureLogRepository.totalWeeklyKilometers(vehicleId);
        Long departures  = departureLogRepository.totalWeeklyDepartures(vehicleId);

        Double consumption = (liters != null && liters > 0) ? km / liters : 0.0;

        VehicleDashboardDataDTO data =
                new VehicleDashboardDataDTO(spending, liters, km, departures, consumption);

        return new VehicleDashboardDTO(
                vehicle.getId(),
                vehicle.getModel(),
                vehicle.getPrefix(),
                Map.of("week", data),
                new VehicleMaintenanceDTO(
                        vehicle.getCurrentMileage() != null
                                ? vehicle.getCurrentMileage().doubleValue() : 0.0,
                        100000.0
                )
        );
    }
}