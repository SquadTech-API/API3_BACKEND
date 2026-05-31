package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.OilChangeAlertDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeResponseDTO;
import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.OilChange;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.DepartureLogRepository;
import br.com.edu.fatec.ipemControl.repository.OilChangeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OilChangeService {

    private final OilChangeRepository oilChangeRepository;
    private final VehicleRepository vehicleRepository;
    private final DepartureLogRepository departureLogRepository;

    // ── POST /oil-changes (#A15) ──────────────────────────────────
    public OilChangeResponseDTO save(OilChangeDTO dto) {
        if (dto.getVehicleId() == null)
            throw new BusinessRuleException("Informe o veículo.");
        if (dto.getChangeMileage() == null)
            throw new BusinessRuleException("Informe o KM da troca.");
        if (dto.getChangeDate() == null)
            throw new BusinessRuleException("Informe a data da troca.");
        if (dto.getDepartureLogId() == null)
            throw new BusinessRuleException("Troca de óleo deve estar vinculada a uma saída.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        DepartureLog departureLog = departureLogRepository.findById(dto.getDepartureLogId())
                .orElseThrow(() -> new ResourceNotFoundException("Saída não encontrada."));

        if (!departureLog.getServiceType().isOilChange())
            throw new BusinessRuleException(
                    "A saída informada não é do tipo troca de óleo.");

        BigDecimal interval = dto.getIntervalKm() != null
                ? dto.getIntervalKm()
                : (vehicle.getOilChangeIntervalKm() != null
                ? vehicle.getOilChangeIntervalKm()
                : new BigDecimal("5000"));

        OilChange oilChange = new OilChange();
        oilChange.setVehicle(vehicle);
        oilChange.setDepartureLog(departureLog);
        oilChange.setChangeMileage(dto.getChangeMileage());
        oilChange.setChangeDate(dto.getChangeDate());
        oilChange.setNotes(dto.getNotes());
        oilChange.setIntervalKm(interval);
        oilChange.setNextChangeMileage(dto.getChangeMileage().add(interval));

        vehicle.setOilChangeIntervalKm(interval);
        vehicle.setOilChangeAlertSent(false);
        vehicleRepository.save(vehicle);

        return toDTO(oilChangeRepository.save(oilChange));
    }

    // ── GET /oil-changes?vehicleId={id} ──────────────────────────
    public List<OilChangeResponseDTO> findByVehicle(Integer vehicleId) {
        if (vehicleId == null)
            return oilChangeRepository.findAll().stream().map(this::toDTO).toList();
        return oilChangeRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(this::toDTO).toList();
    }

    // ── GET /oil-changes/{id} ─────────────────────────────────────
    public OilChangeResponseDTO findById(Integer id) {
        return oilChangeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Troca de óleo não encontrada."));
    }

    // ── PUT /oil-changes/{id} ─────────────────────────────────────
    public OilChangeResponseDTO update(Integer id, OilChangeDTO dto) {
        OilChange oilChange = oilChangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Troca de óleo não encontrada."));

        if (dto.getChangeMileage() != null)  oilChange.setChangeMileage(dto.getChangeMileage());
        if (dto.getIntervalKm() != null)     oilChange.setIntervalKm(dto.getIntervalKm());
        if (dto.getChangeDate() != null)     oilChange.setChangeDate(dto.getChangeDate());
        if (dto.getNotes() != null)          oilChange.setNotes(dto.getNotes());

        if (dto.getChangeMileage() != null && dto.getIntervalKm() != null)
            oilChange.setNextChangeMileage(dto.getChangeMileage().add(dto.getIntervalKm()));

        return toDTO(oilChangeRepository.save(oilChange));
    }

    // ── GET /oil-changes/alerts ───────────────────────────────────
    public List<OilChangeAlertDTO> findAlerts() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<OilChangeAlertDTO> alerts = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            if (!vehicle.isActive()) continue;
            if (vehicle.getCurrentMileage() == null) continue;

            Optional<OilChange> latestOpt =
                    oilChangeRepository.findLatestByVehicle(vehicle.getId());
            if (latestOpt.isEmpty()) continue;

            OilChange latest = latestOpt.get();
            if (latest.getNextChangeMileage() == null) continue;

            BigDecimal current  = vehicle.getCurrentMileage();
            BigDecimal next     = latest.getNextChangeMileage();
            BigDecimal limit500 = next.subtract(BigDecimal.valueOf(500));

            String alertType = null;
            if      (current.compareTo(next) >= 0)     alertType = "overdue";
            else if (current.compareTo(limit500) >= 0) alertType = "approaching";

            if (alertType == null) continue;

            alerts.add(new OilChangeAlertDTO(
                    vehicle.getId(),
                    vehicle.getPrefix(),
                    vehicle.getModel(),
                    vehicle.getLicensePlate(),
                    current,
                    latest.getChangeMileage(),
                    next,
                    current.subtract(next),
                    latest.getChangeDate(),
                    alertType
            ));
        }

        alerts.sort((a, b) -> {
            if (!a.getAlertType().equals(b.getAlertType()))
                return "overdue".equals(a.getAlertType()) ? -1 : 1;
            return b.getKmOverdue().compareTo(a.getKmOverdue());
        });

        return alerts;
    }

    // ── Job agendado — marca alertas todo dia às 06:00 ───────────
    @Scheduled(cron = "0 0 6 * * *")
    public void markAlerts() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle vehicle : vehicles) {
            if (!vehicle.isActive()) continue;
            if (vehicle.getCurrentMileage() == null) continue;

            Optional<OilChange> latestOpt =
                    oilChangeRepository.findLatestByVehicle(vehicle.getId());
            if (latestOpt.isEmpty()) continue;

            OilChange latest = latestOpt.get();
            if (latest.getNextChangeMileage() == null) continue;

            boolean overdue = vehicle.getCurrentMileage()
                    .compareTo(latest.getNextChangeMileage()) >= 0;

            if (overdue && !vehicle.isOilChangeAlertSent()) {
                vehicle.setOilChangeAlertSent(true);
                vehicleRepository.save(vehicle);
            } else if (!overdue && vehicle.isOilChangeAlertSent()) {
                vehicle.setOilChangeAlertSent(false);
                vehicleRepository.save(vehicle);
            }
        }
    }

    // ── Mapeamento ────────────────────────────────────────────────
    private OilChangeResponseDTO toDTO(OilChange o) {
        OilChangeResponseDTO dto = new OilChangeResponseDTO();
        dto.setId(o.getId());
        dto.setChangeMileage(o.getChangeMileage());
        dto.setIntervalKm(o.getIntervalKm());
        dto.setNextChangeMileage(o.getNextChangeMileage());
        dto.setChangeDate(o.getChangeDate());
        dto.setNotes(o.getNotes());
        dto.setAlertSent(o.isAlertSent());
        if (o.getDepartureLog() != null)
            dto.setDepartureLogId(o.getDepartureLog().getId());
        if (o.getVehicle() != null) {
            dto.setVehicleId(o.getVehicle().getId());
            dto.setVehiclePrefix(o.getVehicle().getPrefix());
            dto.setVehicleModel(o.getVehicle().getModel());
            dto.setVehicleLicensePlate(o.getVehicle().getLicensePlate());
        }
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        return dto;
    }
}