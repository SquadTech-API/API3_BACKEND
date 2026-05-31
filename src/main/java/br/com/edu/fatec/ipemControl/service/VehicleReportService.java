package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.VehicleReportDTO;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VehicleReportService {

    private final VehicleRepository vehicleRepository;
    private final DepartureLogRepository departureLogRepository;
    private final FuelingRepository fuelingRepository;

    public VehicleReportDTO generateVehicleReport(Integer vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        var departures = departureLogRepository.findByVehicleIdAndDepartureDatetimeBetween(
                vehicleId,
                LocalDateTime.now().minusYears(5),
                LocalDateTime.now()
        );

        int totalDepartures = departures.size();

        BigDecimal drivenMileage = departures.stream()
                .filter(d -> d.getDrivenMileage() != null)
                .map(d -> d.getDrivenMileage())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var fuelings = fuelingRepository.findByVehicleId(vehicleId);

        BigDecimal totalLiters = fuelings.stream()
                .filter(f -> f.getLiters() != null)
                .map(f -> f.getLiters())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double avgConsumption = 0.0;
        if (totalLiters.compareTo(BigDecimal.ZERO) > 0)
            avgConsumption = drivenMileage.divide(totalLiters, 2, RoundingMode.HALF_UP).doubleValue();

        VehicleReportDTO dto = new VehicleReportDTO();
        dto.setPrefix(vehicle.getPrefix());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setManufactureYear(vehicle.getManufactureYear());
        dto.setFuelTypeName(vehicle.getFuelType() != null ? vehicle.getFuelType().getName() : "—");
        dto.setMileageDriven(drivenMileage.doubleValue());
        dto.setAvgConsumption(avgConsumption);
        dto.setTotalDepartures(totalDepartures);
        return dto;
    }
}