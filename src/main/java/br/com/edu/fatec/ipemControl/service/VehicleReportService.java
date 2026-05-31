package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.VehicleReportDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.Fueling;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleReportService {

    private final VehicleRepository vehicleRepository;
    private final ExitRecordRepository exitRecordRepository;
    private final FuelingRepository refuelingRepository;

    public VehicleReportService(VehicleRepository vehicleRepository,
                                ExitRecordRepository exitRecordRepository,
                                FuelingRepository refuelingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.exitRecordRepository = exitRecordRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public VehicleReportDTO generateVehicleReport(Integer vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        List<DepartureLog> completedDepartures = exitRecordRepository
                .findByVehicleVehicleIdAndDateTimeDepartureBetween(
                        vehicleId,
                        LocalDateTime.now().minusYears(5),
                        LocalDateTime.now()
                );

        int totalDepartures = completedDepartures.size();

        BigDecimal drivenKm = completedDepartures.stream()
                .filter(s -> s.getDrivenKm() != null)
                .map(DepartureLog::getDrivenKm)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Fueling> fuelings = refuelingRepository
                .findByDepartureLogVehicleVehicleIdOrderByDateTimeDesc(vehicleId);

        BigDecimal totalLiters = fuelings.stream()
                .filter(a -> a.getLitersAmount() != null)
                .map(Fueling::getLitersAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double averageConsumption = 0.0;
        if (totalLiters.compareTo(BigDecimal.ZERO) > 0) {
            averageConsumption = drivenKm.divide(totalLiters, 2, RoundingMode.HALF_UP).doubleValue();
        }

        VehicleReportDTO dto = new VehicleReportDTO();
        dto.setPrefix(vehicle.getPrefix());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setYear(vehicle.getYear());
        dto.setFuelType(vehicle.getFuelType());
        dto.setMileageDriven(drivenKm.doubleValue());
        dto.setAvgConsumption(averageConsumption);
        dto.setTotalDepartures(totalDepartures);

        return dto;
    }
}
