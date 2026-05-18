package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.OilChangeDTO;
import br.com.edu.fatec.IPEMControl.DTO.OilChangeResponseDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.OilChange;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.ResourceNotFoundException;
import br.com.edu.fatec.IPEMControl.Exception.BusinessRuleException;
import br.com.edu.fatec.IPEMControl.Repository.ExitRecordRepository;
import br.com.edu.fatec.IPEMControl.Repository.OilChangeRepository;
import br.com.edu.fatec.IPEMControl.Repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * NOVO: Service para gerenciamento de trocas de óleo.
 * Antes não existia — o frontend chamava endpoints que retornavam 404.
 */
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

    // ── POST /oilChange-oleo ──────────────────────────────────────────────────────
    public OilChangeResponseDTO save(OilChangeDTO dto) {

        if (dto.getVehicleId() == null)
            throw new BusinessRuleException("Informe o veículo.");
        if (dto.getOilChangeMileage() == null)
            throw new BusinessRuleException("Informe o KM da oilChange.");
        if (dto.getOilChangeDate() == null)
            throw new BusinessRuleException("Informe a data da oilChange.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        OilChange oilChange = new OilChange();
        oilChange.setVehicle(vehicle);
        oilChange.setChangeKm(dto.getOilChangeMileage());
        oilChange.setChangeDate(dto.getOilChangeDate());
        oilChange.setObservation(dto.getObservations());

        // Intervalo: usa o informado ou o padrão do veículo
        BigDecimal interval = dto.getIntervalKm() != null
                ? dto.getIntervalKm()
                : (vehicle.getOilChangeIntervalKm() != null
                ? vehicle.getOilChangeIntervalKm()
                : new BigDecimal("5000"));
        oilChange.setIntervalKm(interval);

        // Próxima oilChange: usa o informado ou calcula
        BigDecimal nextChange = dto.getNextOilChangeMileage() != null
                ? dto.getNextOilChangeMileage()
                : dto.getOilChangeMileage().add(interval);
        oilChange.setNextChangeKm(nextChange);

        // Vínculo com saída (opcional)
        if (dto.getDepartureId() != null) {
            DepartureLog departureLog = exitRecordRepository.findById(dto.getDepartureId())
                    .orElseThrow(() -> new ResourceNotFoundException("Saída não encontrada."));
            oilChange.setDepartureLog(departureLog);
        }

        // Atualiza interval padrão no veículo para futuros alertas
        vehicle.setOilChangeIntervalKm(interval);
        vehicle.setOilChangeAlertSent(false);
        vehicleRepository.save(vehicle);

        return toDTO(oilChangeRepository.save(oilChange));
    }

    // ── GET /oilChange-oleo?veiculoId={id} ────────────────────────────────────────
    public List<OilChangeResponseDTO> findByVehicle(Integer vehicleId) {
        if (vehicleId == null)
            return oilChangeRepository.findAll().stream().map(this::toDTO).toList();
        return oilChangeRepository.findByVehicleVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ── GET /oilChange-oleo/{id} ──────────────────────────────────────────────────
    public OilChangeResponseDTO findById(Integer id) {
        return oilChangeRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Oil change not found."));
    }

    // ── PUT /oilChange-oleo/{id} ──────────────────────────────────────────────────
    public OilChangeResponseDTO update(Integer id, OilChangeDTO dto) {
        OilChange oilChange = oilChangeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Oil change not found."));

        if (dto.getOilChangeMileage() != null)        oilChange.setChangeKm(dto.getOilChangeMileage());
        if (dto.getIntervalKm() != null)    oilChange.setIntervalKm(dto.getIntervalKm());
        if (dto.getNextOilChangeMileage() != null) oilChange.setNextChangeKm(dto.getNextOilChangeMileage());
        if (dto.getOilChangeDate() != null)      oilChange.setChangeDate(dto.getOilChangeDate());
        if (dto.getObservations() != null)    oilChange.setObservation(dto.getObservations());

        return toDTO(oilChangeRepository.save(oilChange));
    }

    private OilChangeResponseDTO toDTO(OilChange oilChange) {
        OilChangeResponseDTO dto = new OilChangeResponseDTO();
        dto.setOilChangeId(oilChange.getOilChangeId());
        dto.setChangeKm(oilChange.getChangeKm());
        dto.setIntervalKm(oilChange.getIntervalKm());
        dto.setNextChangeKm(oilChange.getNextChangeKm());
        dto.setChangeDate(oilChange.getChangeDate());
        dto.setObservation(oilChange.getObservation());
        dto.setAlertSent(oilChange.getSendAlert());
        if (oilChange.getDepartureLog() != null) {
            dto.setDepartureLogId(oilChange.getDepartureLog().getDepartureLogId());
        }
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
