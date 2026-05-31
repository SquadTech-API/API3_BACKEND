package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.VehicleDTO;
import br.com.edu.fatec.ipemControl.dto.VehicleSummaryDTO;
import br.com.edu.fatec.ipemControl.entities.DepartureLog;
import br.com.edu.fatec.ipemControl.entities.Fueling;
import br.com.edu.fatec.ipemControl.entities.Vehicle;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.RefuelingRepository;
import br.com.edu.fatec.ipemControl.repository.ExitRecordRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yy");

    private static final DecimalFormat FMT_KM;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        FMT_KM = new DecimalFormat("#,###", symbols);
    }

    private final VehicleRepository vehicleRepository;
    private final ExitRecordRepository exitRecordRepository;
    private final RefuelingRepository refuelingRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          ExitRecordRepository exitRecordRepository,
                          RefuelingRepository refuelingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.exitRecordRepository = exitRecordRepository;
        this.refuelingRepository = refuelingRepository;
    }

    /**
     * Lista veículos com summary.
     * CORRIGIDO: parâmetro "includeAll" — quando false filtra apenas veículos ativos (active=true).
     * ADM usa ?includeAll=true para ver includeAll incluindo inativos.
     */
    public List<VehicleSummaryDTO> findVehicleSummaries(boolean includeAll) {

        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                // CORRIGIDO: técnico não vê veículos inativos
                .filter(v -> includeAll || Boolean.TRUE.equals(v.getActive()))
                .collect(Collectors.toList());

        return vehicles.stream().map(vehicle -> {

            Optional<DepartureLog> latestDeparture =
                    exitRecordRepository
                            .findTopByVehicleVehicleIdOrderByDateTimeDepartureDesc(vehicle.getVehicleId());

            boolean inUse = latestDeparture
                    .map(r -> "em_andamento".equalsIgnoreCase(r.getStatus()))
                    .orElse(false);

            String latestUsage = latestDeparture
                    .map(r -> formatDate(r.getDateTimeDeparture()))
                    .orElse("—");

            String latestDriver = latestDeparture
                    .map(r -> r.getUser() != null ? r.getUser().getName() : "—")
                    .orElse("—");

            Optional<Fueling> latestFueling =
                    refuelingRepository
                            .findTopByDepartureLogVehicleVehicleIdOrderByDateTimeDesc(vehicle.getVehicleId());

            String latestFuelingText = latestFueling
                    .map(a -> formatDate(a.getDateTime()))
                    .orElse("—");

            String km = vehicle.getCurrentKm() != null
                    ? FMT_KM.format(vehicle.getCurrentKm().longValue())
                    : "—";

            String status = inUse ? "em_uso" : "disponivel";

            // CORRIGIDO: VehicleSummaryDTO agora inclui licenseCategory e active
            VehicleSummaryDTO dto = new VehicleSummaryDTO(
                    vehicle.getVehicleId(),
                    vehicle.getModel(),
                    vehicle.getPrefix(),
                    latestUsage,
                    latestDriver,
                    latestFuelingText,
                    km,
                    status
            );
            dto.setLicenseCategory(vehicle.getLicenseCategory());
            dto.setActive(vehicle.getActive());
            return dto;

        }).collect(Collectors.toList());
    }

    public List<VehicleDTO> findAll() {
        return vehicleRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public VehicleDTO findById(Integer id) {
        return vehicleRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));
    }

    public VehicleDTO create(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        apply(dto, vehicle);
        return toDTO(vehicleRepository.save(vehicle));
    }

    public void delete(Integer id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found.");
        }
        vehicleRepository.deleteById(id);
    }

    /**
     * Ativa ou desativa um veículo.
     * NOVO: endpoint /vehicles/{id}/ativar e /vehicles/{id}/desativar
     */
    public VehicleDTO toggleActive(Integer id, boolean active) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));
        vehicle.setActive(active);
        return toDTO(vehicleRepository.save(vehicle));
    }

    /**
     * Atualiza data de um veículo (PUT completo).
     * NOVO: endpoint /vehicles/{id} PUT
     */
    public VehicleDTO update(Integer id, VehicleDTO updated) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        apply(updated, vehicle);
        return toDTO(vehicleRepository.save(vehicle));
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "—";
        return dateTime.format(FMT_DATA);
    }

    private void apply(VehicleDTO dto, Vehicle vehicle) {
        vehicle.setPrefix(dto.getPrefix());
        vehicle.setDarCenter(dto.getDarCenter());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setModel(dto.getModel());
        vehicle.setBrand(dto.getBrand());
        vehicle.setYear(dto.getYear());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setLicenseCategory(dto.getLicenseCategory());
        vehicle.setCurrentKm(dto.getCurrentKm());
        if (dto.getAvailable() != null) vehicle.setAvailable(dto.getAvailable());
        if (dto.getActive() != null) vehicle.setActive(dto.getActive());
        if (dto.getFlNumber() != null) vehicle.setFlNumber(dto.getFlNumber());
        if (dto.getOilChangeIntervalKm() != null) vehicle.setOilChangeIntervalKm(dto.getOilChangeIntervalKm());
        if (dto.getOilChangeAlertSent() != null) vehicle.setOilChangeAlertSent(dto.getOilChangeAlertSent());
    }

    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setVehicleId(vehicle.getVehicleId());
        dto.setPrefix(vehicle.getPrefix());
        dto.setDarCenter(vehicle.getDarCenter());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setModel(vehicle.getModel());
        dto.setBrand(vehicle.getBrand());
        dto.setYear(vehicle.getYear());
        dto.setFuelType(vehicle.getFuelType());
        dto.setLicenseCategory(vehicle.getLicenseCategory());
        dto.setCurrentKm(vehicle.getCurrentKm());
        dto.setAvailable(vehicle.getAvailable());
        dto.setActive(vehicle.getActive());
        dto.setFlNumber(vehicle.getFlNumber());
        dto.setOilChangeIntervalKm(vehicle.getOilChangeIntervalKm());
        dto.setOilChangeAlertSent(vehicle.getOilChangeAlertSent());
        dto.setCreatedAt(vehicle.getCreatedAt());
        dto.setUpdatedAt(vehicle.getUpdatedAt());
        return dto;
    }
}
