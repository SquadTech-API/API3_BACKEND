package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.*;
import br.com.edu.fatec.ipemControl.entity.*;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartureLogService {

    private final DepartureLogRepository departureLogRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final FuelingRepository fuelingRepository;

    // ── POST /departure-logs — abre saída ────────────────────────
    public DepartureLogResponseDTO openDeparture(DepartureLogDTO dto) {
        if (dto.getVehicleId() == null)
            throw new BusinessRuleException("Informe o veículo.");
        if (dto.getUserRegistration() == null)
            throw new BusinessRuleException("Informe o usuário.");
        if (dto.getServiceTypeId() == null)
            throw new BusinessRuleException("Informe o tipo de serviço.");
        if (dto.getInitialMileage() == null)
            throw new BusinessRuleException("Informe o KM inicial.");
        if (dto.getInitialMileage().compareTo(BigDecimal.ZERO) < 0)
            throw new BusinessRuleException("KM inicial não pode ser negativo.");
        if (dto.getDepartureDatetime() == null)
            throw new BusinessRuleException("Informe a data e hora de saída.");
        if (dto.getDestination() == null || dto.getDestination().isBlank())
            throw new BusinessRuleException("Informe o local de destino.");

        if (departureLogRepository.existsActiveByUserRegistration(dto.getUserRegistration()))
            throw new BusinessRuleException(
                    "Você já possui uma saída em andamento. Registre o retorno antes de iniciar uma nova.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        if (!vehicle.isAvailable())
            throw new BusinessRuleException("Veículo não está disponível.");

        if (vehicle.getCurrentMileage() != null &&
                dto.getInitialMileage().compareTo(vehicle.getCurrentMileage()) < 0)
            throw new BusinessRuleException(
                    "KM inicial não pode ser menor que o KM atual do veículo (" +
                            vehicle.getCurrentMileage() + ").");

        User user = userRepository.findByRegistration(dto.getUserRegistration())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!user.isActiveEmployee())
            throw new BusinessRuleException("Colaborador inativo.");

        ServiceType serviceType = serviceTypeRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));

        DepartureLog departureLog = new DepartureLog();
        departureLog.setVehicle(vehicle);
        departureLog.setUser(user);
        departureLog.setServiceType(serviceType);
        departureLog.setDestination(dto.getDestination());
        departureLog.setNotes(dto.getNotes());
        departureLog.setStartingMileage(dto.getInitialMileage());
        departureLog.setDepartureDatetime(dto.getDepartureDatetime());
        departureLog.setStatus("in_progress");

        if (dto.getSecondUserRegistration() != null) {
            User secondUser = userRepository.findByRegistration(dto.getSecondUserRegistration())
                    .orElseThrow(() -> new ResourceNotFoundException("2º condutor não encontrado."));
            departureLog.setSecondUser(secondUser);
        }

        vehicle.setAvailable(false);
        vehicleRepository.save(vehicle);

        return toDTO(departureLogRepository.save(departureLog));
    }

    // ── PATCH /departure-logs/{id}/return ────────────────────────
    public DepartureLogResponseDTO registerReturn(Integer id, ReturnDTO dto) {
        if (dto.getFinalMileage() == null)
            throw new BusinessRuleException("Informe o KM final.");
        if (dto.getReturnDatetime() == null)
            throw new BusinessRuleException("Informe o horário de chegada.");

        DepartureLog departureLog = departureLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de saída não encontrado."));

        if (!"in_progress".equalsIgnoreCase(departureLog.getStatus()))
            throw new BusinessRuleException("Esta saída já foi encerrada.");
        if (dto.getFinalMileage().compareTo(departureLog.getStartingMileage()) < 0)
            throw new BusinessRuleException("KM final não pode ser menor que o KM inicial.");
        if (dto.getReturnDatetime().isBefore(departureLog.getDepartureDatetime()))
            throw new BusinessRuleException("Horário de chegada não pode ser anterior ao horário de saída.");

        BigDecimal drivenMileage = dto.getFinalMileage().subtract(departureLog.getStartingMileage());

        departureLog.setFinishingMileage(dto.getFinalMileage());
        departureLog.setDrivenMileage(drivenMileage);
        departureLog.setReturnDatetime(dto.getReturnDatetime());
        departureLog.setStatus("completed");

        if (dto.getNotes() != null && !dto.getNotes().isBlank())
            departureLog.setNotes(dto.getNotes());

        Vehicle vehicle = departureLog.getVehicle();
        vehicle.setCurrentMileage(dto.getFinalMileage());
        vehicle.setAvailable(true);
        vehicleRepository.save(vehicle);

        return toDTO(departureLogRepository.save(departureLog));
    }

    // ── PATCH /departure-logs/{id}/mark-transcribed (#A09) ───────
    public DepartureLogResponseDTO markAsSgiTranscribed(Integer id) {
        DepartureLog departureLog = departureLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de saída não encontrado."));

        if (!"completed".equalsIgnoreCase(departureLog.getStatus()))
            throw new BusinessRuleException("Somente saídas concluídas podem ser marcadas como transcritas ao SGI.");

        departureLog.setSgiTranscribed(true);
        return toDTO(departureLogRepository.save(departureLog));
    }

    // ── GET /departure-logs ───────────────────────────────────────
    public List<DepartureLogResponseDTO> findAll() {
        return departureLogRepository.findAll().stream().map(this::toDTO).toList();
    }

    // ── GET /departure-logs/{id} ──────────────────────────────────
    public DepartureLogResponseDTO findById(Integer id) {
        return departureLogRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de saída não encontrado."));
    }

    // ── GET /departure-logs/active?vehicleId={} ───────────────────
    public DepartureLogResponseDTO findActiveByVehicle(Integer vehicleId) {
        return departureLogRepository
                .findTopByVehicleIdAndStatusOrderByDepartureDatetimeDesc(vehicleId, "in_progress")
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma saída ativa para este veículo."));
    }

    // ── GET /departure-logs/active-user?registration={} ──────────
    public DepartureLogResponseDTO findActiveByUser(Integer registration) {
        return departureLogRepository
                .findTopByUserRegistrationAndStatusOrderByDepartureDatetimeDesc(registration, "in_progress")
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma saída ativa para este usuário."));
    }

    // ── GET /departure-logs/not-transcribed ───────────────────────
    public List<DepartureLogResponseDTO> findNotTranscribed() {
        return departureLogRepository
                .findBySgiTranscribedFalseAndStatusOrderByDepartureDatetimeDesc("completed")
                .stream().map(this::toDTO).toList();
    }

    // ── GET /departure-logs/vehicle/{vehicleId}/oil-change ────────
    public List<DepartureLogResponseDTO> findOilChangeDeparturesByVehicle(Integer vehicleId) {
        return departureLogRepository
                .findByVehicleIdAndServiceTypeIsOilChangeTrueOrderByDepartureDatetimeDesc(vehicleId)
                .stream().map(this::toDTO).toList();
    }

    // ── Relatório mensal por viatura ──────────────────────────────
    public MonthlyUsageReportDTO generateMonthlyReport(Integer vehicleId,
                                                       LocalDateTime start,
                                                       LocalDateTime end) {
        List<DepartureLog> trips = departureLogRepository
                .findByVehicleIdAndDepartureDatetimeBetween(vehicleId, start, end);

        BigDecimal totalKm       = BigDecimal.ZERO;
        BigDecimal totalSpending = BigDecimal.ZERO;
        BigDecimal totalLiters   = BigDecimal.ZERO;

        for (DepartureLog trip : trips) {
            if (trip.getDrivenMileage() != null)
                totalKm = totalKm.add(trip.getDrivenMileage());

            for (var f : fuelingRepository.findByDepartureLog(trip)) {
                if (f.getTotalValue() != null) totalSpending = totalSpending.add(f.getTotalValue());
                if (f.getLiters()     != null) totalLiters   = totalLiters.add(f.getLiters());
            }
        }

        // Converte entidades para DTOs — nunca expor entidade diretamente
        List<DepartureLogResponseDTO> details = trips.stream().map(this::toDTO).toList();

        return new MonthlyUsageReportDTO(totalKm, trips.size(), totalSpending, totalLiters, details);
    }

    // ── Mapeamento ────────────────────────────────────────────────
    public DepartureLogResponseDTO toDTO(DepartureLog d) {
        DepartureLogResponseDTO dto = new DepartureLogResponseDTO();
        dto.setId(d.getId());
        dto.setDestination(d.getDestination());
        dto.setStatus(d.getStatus());
        dto.setNotes(d.getNotes());
        dto.setDepartureDatetime(d.getDepartureDatetime());
        dto.setReturnDatetime(d.getReturnDatetime());
        dto.setStartingMileage(d.getStartingMileage());
        dto.setFinishingMileage(d.getFinishingMileage());
        dto.setDrivenMileage(d.getDrivenMileage());
        dto.setSgiTranscribed(d.isSgiTranscribed());

        if (d.getVehicle() != null) {
            dto.setVehicleId(d.getVehicle().getId());
            dto.setVehiclePrefix(d.getVehicle().getPrefix());
            dto.setVehicleModel(d.getVehicle().getModel());
            dto.setVehicleLicensePlate(d.getVehicle().getLicensePlate());
        }
        if (d.getUser() != null) {
            dto.setUserRegistration(d.getUser().getRegistration());
            dto.setUserName(d.getUser().getFullName());
        }
        if (d.getSecondUser() != null) {
            dto.setSecondUserRegistration(d.getSecondUser().getRegistration());
            dto.setSecondUserName(d.getSecondUser().getFullName());
        }
        if (d.getServiceType() != null) {
            dto.setServiceTypeId(d.getServiceType().getId());
            dto.setServiceName(d.getServiceType().getServiceName());
        }
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }
}