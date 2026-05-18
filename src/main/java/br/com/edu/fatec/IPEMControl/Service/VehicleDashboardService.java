package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.RefuelingRepository;
import br.com.edu.fatec.IPEMControl.Repository.ExitRecordRepository;
import br.com.edu.fatec.IPEMControl.Repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class VehicleDashboardService {

    private final ExitRecordRepository exitRecordRepository;
    private final RefuelingRepository refuelingRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleDashboardService(ExitRecordRepository exitRecordRepository,
                                   RefuelingRepository refuelingRepository,
                                   VehicleRepository vehicleRepository) {
        this.exitRecordRepository = exitRecordRepository;
        this.refuelingRepository = refuelingRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleDashboardResponseDTO findDashboard() {

        List<KilometerChartItemDTO> weeklyTopVehicles = exitRecordRepository.findTop5WeeklyKilometers()
                .stream()
                .map(obj -> new KilometerChartItemDTO(
                        ((Number) obj[0]).intValue(),
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue()
                ))
                .toList();

        if (weeklyTopVehicles.isEmpty()) {
            return new VehicleDashboardResponseDTO(Map.of("semana", new ArrayList<>()), null);
        }

        Integer defaultVehicleId = weeklyTopVehicles.get(0).getId();

        VehicleDashboardDTO defaultVehicle = buildVehicleDashboard(defaultVehicleId);

        return new VehicleDashboardResponseDTO(
                Map.of("semana", weeklyTopVehicles),
                defaultVehicle
        );
    }

    private VehicleDashboardDTO buildVehicleDashboard(Integer vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElseThrow();

        Double spending = refuelingRepository.totalWeeklySpending(vehicleId);
        Double liters = refuelingRepository.totalWeeklyLiters(vehicleId);
        Double km = exitRecordRepository.totalWeeklyKilometers(vehicleId);
        Long departures = exitRecordRepository.totalWeeklyDepartures(vehicleId);

        Double consumption = (liters != null && liters > 0) ? km / liters : 0.0;

        VehicleDashboardDataDTO data =
                new VehicleDashboardDataDTO(spending, liters, km, departures, consumption);

        return new VehicleDashboardDTO(
                vehicle.getVehicleId(),
                vehicle.getModel(),
                vehicle.getPrefix(),
                Map.of("semana", data),
                new VehicleMaintenanceDTO(
                        vehicle.getCurrentKm() != null ? vehicle.getCurrentKm().doubleValue() : 0.0,
                        100000.0
                )
        );
    }
}
