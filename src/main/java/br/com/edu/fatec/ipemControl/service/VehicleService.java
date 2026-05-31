package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.VehicleDTO;
import br.com.edu.fatec.ipemControl.dto.VehicleSummaryDTO;
import br.com.edu.fatec.ipemControl.entity.FuelType;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.FuelTypeRepository;
import br.com.edu.fatec.ipemControl.repository.FuelingRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DecimalFormat FMT_KM;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        FMT_KM = new DecimalFormat("#,###", symbols);
    }

    private final VehicleRepository vehicleRepository;
    private final DepartureLogRepository departureLogRepository;
    private final FuelingRepository fuelingRepository;
    private final FuelTypeRepository fuelTypeRepository;

    // ── GET /vehicles — lista com summary ────────────────────────
    public List<VehicleSummaryDTO> findVehicleSummaries(boolean includeAll) {
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> includeAll || v.isActive())
                .collect(Collectors.toList());

        return vehicles.stream().map(vehicle -> {

            var latestDeparture = departureLogRepository
                    .findTopByVehicleIdAndStatusOrderByDepartureDatetimeDesc(
                            vehicle.getId(), "in_progress");

            boolean inUse = latestDeparture.isPresent();

            String latestUsage = latestDeparture
                    .map(d -> d.getDepartureDatetime() != null
                            ? d.getDepartureDatetime().format(FMT_DATE) : "—")
                    .orElse("—");

            String latestDriver = latestDeparture
                    .map(d -> d.getUser() != null ? d.getUser().getFullName() : "—")
                    .orElse("—");

            var latestFueling = fuelingRepository
                    .findTopByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(vehicle.getId());

            String latestFuelingText = latestFueling
                    .map(f -> f.getFuelingDatetime() != null
                            ? f.getFuelingDatetime().format(FMT_DATE) : "—")
                    .orElse("—");

            String km = vehicle.getCurrentMileage() != null
                    ? FMT_KM.format(vehicle.getCurrentMileage().longValue())
                    : "—";

            String status = inUse ? "in_use" : "available";

            VehicleSummaryDTO dto = new VehicleSummaryDTO(
                    vehicle.getId(),
                    vehicle.getModel(),
                    vehicle.getPrefix(),
                    latestUsage,
                    latestDriver,
                    latestFuelingText,
                    km,
                    status
            );
            dto.setLicenseCategory(vehicle.getLicenseCategory());
            dto.setActive(vehicle.isActive());
            return dto;

        }).collect(Collectors.toList());
    }

    // ── GET /vehicles (lista completa) ───────────────────────────
    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream().map(this::toDTO).toList();
    }

    // ── GET /vehicles/{id} ───────────────────────────────────────
    public VehicleDTO findById(Integer id) {
        return vehicleRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));
    }

    // ── POST /vehicles ───────────────────────────────────────────
    public VehicleDTO create(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        apply(dto, vehicle);
        return toDTO(vehicleRepository.save(vehicle));
    }

    // ── PUT /vehicles/{id} ───────────────────────────────────────
    public VehicleDTO update(Integer id, VehicleDTO dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));
        apply(dto, vehicle);
        return toDTO(vehicleRepository.save(vehicle));
    }

    // ── PATCH /vehicles/{id}/activate | deactivate ───────────────
    public VehicleDTO toggleActive(Integer id, boolean active) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        if (!active && !vehicle.isAvailable())
            throw new BusinessRuleException("Não é possível desativar uma viatura com saída em andamento.");

        vehicle.setActive(active);
        return toDTO(vehicleRepository.save(vehicle));
    }

    // ── DELETE /vehicles/{id} ────────────────────────────────────
    public void delete(Integer id) {
        if (!vehicleRepository.existsById(id))
            throw new ResourceNotFoundException("Veículo não encontrado.");
        vehicleRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────────────
    private void apply(VehicleDTO dto, Vehicle vehicle) {
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setDarCenter(dto.getDarCenter());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setModel(dto.getModel());
        vehicle.setBrand(dto.getBrand());
        vehicle.setManufactureYear(dto.getManufactureYear());
        vehicle.setLicenseCategory(dto.getLicenseCategory());
        vehicle.setCurrentMileage(dto.getCurrentMileage());
        if (dto.getFlNumber() != null) vehicle.setFlNumber(dto.getFlNumber());
        if (dto.getAvailable() != null) vehicle.setAvailable(dto.getAvailable());
        if (dto.getActive() != null) vehicle.setActive(dto.getActive());
        if (dto.getOilChangeIntervalKm() != null) vehicle.setOilChangeIntervalKm(dto.getOilChangeIntervalKm());

        if (dto.getFuelTypeId() != null) {
            FuelType fuelType = fuelTypeRepository.findById(dto.getFuelTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de combustível não encontrado."));
            vehicle.setFuelType(fuelType);
        }
    }

    public VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setPrefix(vehicle.getPrefix());
        dto.setDarCenter(vehicle.getDarCenter());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setModel(vehicle.getModel());
        dto.setBrand(vehicle.getBrand());
        dto.setManufactureYear(vehicle.getManufactureYear());
        dto.setLicenseCategory(vehicle.getLicenseCategory());
        dto.setCurrentMileage(vehicle.getCurrentMileage());
        dto.setAvailable(vehicle.isAvailable());
        dto.setActive(vehicle.isActive());
        dto.setFlNumber(vehicle.getFlNumber());
        dto.setOilChangeIntervalKm(vehicle.getOilChangeIntervalKm());
        dto.setNextOilChangeMileage(vehicle.getNextOilChangeMileage());
        dto.setOilChangeAlertSent(vehicle.isOilChangeAlertSent());
        if (vehicle.getFuelType() != null) {
            dto.setFuelTypeId(vehicle.getFuelType().getId());
            dto.setFuelTypeName(vehicle.getFuelType().getName());
        }
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}