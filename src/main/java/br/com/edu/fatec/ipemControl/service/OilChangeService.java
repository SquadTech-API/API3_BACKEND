package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.OilChangeAlertDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeResponseDTO;
import br.com.edu.fatec.ipemControl.entities.DepartureLog;
import br.com.edu.fatec.ipemControl.entities.OilChange;
import br.com.edu.fatec.ipemControl.entities.Vehicle;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.ExitRecordRepository;
import br.com.edu.fatec.ipemControl.repository.OilChangeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OilChangeService {

    private final OilChangeRepository oilChangeRepository;
    private final VehicleRepository vehicleRepository;
    private final ExitRecordRepository exitRecordRepository;

    public OilChangeService(OilChangeRepository oilChangeRepository,
                            VehicleRepository vehicleRepository,
                            ExitRecordRepository exitRecordRepository) {
        this.oilChangeRepository = oilChangeRepository;
        this.vehicleRepository = vehicleRepository;
        this.exitRecordRepository = exitRecordRepository;
    }

    // ── POST /oil-changes ─────────────────────────────────────────────────────
    public OilChangeResponseDTO save(OilChangeDTO dto) {

        if (dto.getVehicleId() == null)
            throw new BusinessRuleException("Informe o veículo.");
        if (dto.getOilChangeMileage() == null)
            throw new BusinessRuleException("Informe o KM da troca.");
        if (dto.getOilChangeDate() == null)
            throw new BusinessRuleException("Informe a data da troca.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        OilChange oilChange = new OilChange();
        oilChange.setVehicle(vehicle);
        oilChange.setChangeKm(dto.getOilChangeMileage());
        oilChange.setChangeDate(dto.getOilChangeDate());
        oilChange.setObservation(dto.getObservations());

        BigDecimal interval = dto.getIntervalKm() != null
                ? dto.getIntervalKm()
                : (vehicle.getOilChangeIntervalKm() != null
                   ? vehicle.getOilChangeIntervalKm()
                   : new BigDecimal("5000"));
        oilChange.setIntervalKm(interval);

        BigDecimal nextChange = dto.getNextOilChangeMileage() != null
                ? dto.getNextOilChangeMileage()
                : dto.getOilChangeMileage().add(interval);
        oilChange.setNextChangeKm(nextChange);

        if (dto.getDepartureId() != null) {
            DepartureLog departureLog = exitRecordRepository.findById(dto.getDepartureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Saída não encontrada."));
            oilChange.setDepartureLog(departureLog);
        }

        vehicle.setOilChangeIntervalKm(interval);
        vehicle.setOilChangeAlertSent(false);
        vehicleRepository.save(vehicle);

        return toDTO(oilChangeRepository.save(oilChange));
    }

    // ── GET /oil-changes?vehicleId={id} ──────────────────────────────────────
    public List<OilChangeResponseDTO> findByVehicle(Integer vehicleId) {
        if (vehicleId == null)
            return oilChangeRepository.findAll().stream().map(this::toDTO).toList();
        return oilChangeRepository.findByVehicleVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(this::toDTO).toList();
    }

    // ── GET /oil-changes/{id} ─────────────────────────────────────────────────
    public OilChangeResponseDTO findById(Integer id) {
        return oilChangeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Troca de óleo não encontrada."));
    }

    // ── PUT /oil-changes/{id} ─────────────────────────────────────────────────
    public OilChangeResponseDTO update(Integer id, OilChangeDTO dto) {
        OilChange oilChange = oilChangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Troca de óleo não encontrada."));

        if (dto.getOilChangeMileage() != null)     oilChange.setChangeKm(dto.getOilChangeMileage());
        if (dto.getIntervalKm() != null)           oilChange.setIntervalKm(dto.getIntervalKm());
        if (dto.getNextOilChangeMileage() != null) oilChange.setNextChangeKm(dto.getNextOilChangeMileage());
        if (dto.getOilChangeDate() != null)        oilChange.setChangeDate(dto.getOilChangeDate());
        if (dto.getObservations() != null)         oilChange.setObservation(dto.getObservations());

        return toDTO(oilChangeRepository.save(oilChange));
    }

    // ── GET /oil-changes/alerts ───────────────────────────────────────────────
    /**
     * Retorna veículos com troca de óleo atrasada ou se aproximando do limite.
     *
     * alertType "overdue"    → currentKm >= nextChangeMileage (já passou)
     * alertType "approaching" → currentKm >= nextChangeMileage - 500 (faltam ≤ 500 km)
     *
     * Só aparecem veículos com pelo menos uma troca registrada.
     */
    public List<OilChangeAlertDTO> findAlerts() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<OilChangeAlertDTO> alerts = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            if (!Boolean.TRUE.equals(vehicle.getActive())) continue;
            if (vehicle.getCurrentKm() == null) continue;

            Optional<OilChange> latestOpt =
                    oilChangeRepository.findLatestByVehicle(vehicle.getVehicleId());

            if (latestOpt.isEmpty()) continue;

            OilChange latest = latestOpt.get();
            if (latest.getNextChangeKm() == null) continue;

            BigDecimal current  = vehicle.getCurrentKm();
            BigDecimal next     = latest.getNextChangeKm();
            BigDecimal limit500 = next.subtract(BigDecimal.valueOf(500));

            String alertType = null;

            if (current.compareTo(next) >= 0) {
                alertType = "overdue";
            } else if (current.compareTo(limit500) >= 0) {
                alertType = "approaching";
            }

            if (alertType == null) continue;

            BigDecimal kmOverdue = current.subtract(next);

            alerts.add(new OilChangeAlertDTO(
                    vehicle.getVehicleId(),
                    vehicle.getPrefix(),
                    vehicle.getModel(),
                    vehicle.getLicensePlate(),
                    current,
                    latest.getChangeKm(),
                    next,
                    kmOverdue,
                    latest.getChangeDate(),
                    alertType
            ));
        }

        // Ordena: atrasados primeiro, depois por km excedido desc
        alerts.sort((a, b) -> {
            if (!a.getAlertType().equals(b.getAlertType())) {
                return "overdue".equals(a.getAlertType()) ? -1 : 1;
            }
            return b.getKmOverdue().compareTo(a.getKmOverdue());
        });

        return alerts;
    }

    // ── Job agendado — marca oilChangeAlertSent=true nos veículos em atraso ──
    /**
     * Roda todo dia às 06:00.
     * Marca o campo alerta_troca_oleo_enviado=true nos veículos que
     * já passaram do km de troca — pronto para integrar com e-mail/push
     * futuramente só consultando esse campo.
     *
     * Para ativar o scheduling adicione @EnableScheduling na classe principal
     * IPEMControlApplication.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void marcarAlertasEnviados() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            if (!Boolean.TRUE.equals(vehicle.getActive())) continue;
            if (vehicle.getCurrentKm() == null) continue;

            Optional<OilChange> latestOpt =
                    oilChangeRepository.findLatestByVehicle(vehicle.getVehicleId());

            if (latestOpt.isEmpty()) continue;

            OilChange latest = latestOpt.get();
            if (latest.getNextChangeKm() == null) continue;

            boolean overdue = vehicle.getCurrentKm()
                    .compareTo(latest.getNextChangeKm()) >= 0;

            // Só atualiza quando o estado muda para evitar writes desnecessários
            if (overdue && !Boolean.TRUE.equals(vehicle.getOilChangeAlertSent())) {
                vehicle.setOilChangeAlertSent(true);
                vehicleRepository.save(vehicle);
            } else if (!overdue && Boolean.TRUE.equals(vehicle.getOilChangeAlertSent())) {
                // Veículo fez a troca: reseta o flag
                vehicle.setOilChangeAlertSent(false);
                vehicleRepository.save(vehicle);
            }
        }
    }

    // ── Mapeamento ────────────────────────────────────────────────────────────
    private OilChangeResponseDTO toDTO(OilChange oilChange) {
        OilChangeResponseDTO dto = new OilChangeResponseDTO();
        dto.setOilChangeId(oilChange.getOilChangeId());
        dto.setChangeKm(oilChange.getChangeKm());
        dto.setIntervalKm(oilChange.getIntervalKm());
        dto.setNextChangeKm(oilChange.getNextChangeKm());
        dto.setChangeDate(oilChange.getChangeDate());
        dto.setObservation(oilChange.getObservation());
        dto.setAlertSent(oilChange.getSendAlert());
        if (oilChange.getDepartureLog() != null)
            dto.setDepartureLogId(oilChange.getDepartureLog().getDepartureLogId());
        if (oilChange.getVehicle() != null) {
            dto.setVehicleId(oilChange.getVehicle().getVehicleId());
            dto.setVehiclePrefix(oilChange.getVehicle().getPrefix());
            dto.setVehicleModel(oilChange.getVehicle().getModel());
            dto.setVehicleLicensePlate(oilChange.getVehicle().getLicensePlate());
        }
        dto.setCreatedAt(oilChange.getCreatedAt());
        dto.setUpdatedAt(oilChange.getUpdatedAt());
        return dto;
    }
}