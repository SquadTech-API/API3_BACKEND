package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ScheduleDTO;
import br.com.edu.fatec.ipemControl.dto.ScheduleResponseDTO;
import br.com.edu.fatec.ipemControl.entity.*;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final NotificationService notificationService;

    // ── POST /schedules (#T05) ────────────────────────────────────
    public ScheduleResponseDTO create(ScheduleDTO dto, Integer requesterRegistration) {
        User requester = userRepository.findByRegistration(requesterRegistration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        if (!vehicle.isActive())
            throw new BusinessRuleException("Veículo inativo.");

        Schedule schedule = new Schedule();
        schedule.setRequester(requester);
        schedule.setVehicle(vehicle);
        schedule.setTitle(dto.getTitle());
        schedule.setScheduledDatetime(dto.getScheduledDatetime());
        schedule.setPriority(dto.getPriority() != null ? dto.getPriority() : "medium");
        schedule.setEstimatedMileage(dto.getEstimatedMileage());
        schedule.setStatus("pending");

        if (dto.getServiceTypeId() != null) {
            ServiceType serviceType = serviceTypeRepository.findById(dto.getServiceTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de serviço não encontrado."));
            schedule.setServiceType(serviceType);
        }

        return toDTO(scheduleRepository.save(schedule));
    }

    // ── GET /schedules/my (#T05) ──────────────────────────────────
    public List<ScheduleResponseDTO> findByRequester(Integer registration) {
        return scheduleRepository
                .findByRequesterRegistrationOrderByScheduledDatetimeDesc(registration)
                .stream().map(this::toDTO).toList();
    }

    // ── GET /schedules — todos (admin) ────────────────────────────
    public List<ScheduleResponseDTO> findAll() {
        return scheduleRepository.findAllByOrderByScheduledDatetimeDesc()
                .stream().map(this::toDTO).toList();
    }

    // ── GET /schedules?status={status} ───────────────────────────
    public List<ScheduleResponseDTO> findByStatus(String status) {
        return scheduleRepository.findByStatusOrderByScheduledDatetimeDesc(status)
                .stream().map(this::toDTO).toList();
    }

    // ── PATCH /schedules/{id}/approve (#A13) ─────────────────────
    public ScheduleResponseDTO approve(Integer id, Integer approverRegistration) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        if (!"pending".equals(schedule.getStatus()))
            throw new BusinessRuleException("Somente agendamentos pendentes podem ser aprovados.");

        User approver = userRepository.findByRegistration(approverRegistration)
                .orElseThrow(() -> new ResourceNotFoundException("Aprovador não encontrado."));

        schedule.setStatus("confirmed");
        schedule.setApprover(approver);

        // Notifica o técnico
        notificationService.create(
                schedule.getRequester().getRegistration(),
                "schedule_approval",
                "Agendamento confirmado",
                "Seu agendamento \"" + schedule.getTitle() + "\" foi aprovado.",
                null,
                schedule.getId()
        );

        return toDTO(scheduleRepository.save(schedule));
    }

    // ── PATCH /schedules/{id}/reject (#A13) ──────────────────────
    public ScheduleResponseDTO reject(Integer id, String reason, Integer approverRegistration) {
        if (reason == null || reason.isBlank())
            throw new BusinessRuleException("O motivo da recusa é obrigatório.");

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado."));

        if (!"pending".equals(schedule.getStatus()))
            throw new BusinessRuleException("Somente agendamentos pendentes podem ser recusados.");

        User approver = userRepository.findByRegistration(approverRegistration)
                .orElseThrow(() -> new ResourceNotFoundException("Aprovador não encontrado."));

        schedule.setStatus("rejected");
        schedule.setRejectionReason(reason);
        schedule.setApprover(approver);

        // Notifica o técnico com o motivo
        notificationService.create(
                schedule.getRequester().getRegistration(),
                "schedule_rejection",
                "Agendamento recusado",
                "Seu agendamento \"" + schedule.getTitle() + "\" foi recusado.",
                reason,
                schedule.getId()
        );

        return toDTO(scheduleRepository.save(schedule));
    }

    // ── Mapeamento ────────────────────────────────────────────────
    private ScheduleResponseDTO toDTO(Schedule s) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setScheduledDatetime(s.getScheduledDatetime());
        dto.setPriority(s.getPriority());
        dto.setStatus(s.getStatus());
        dto.setRejectionReason(s.getRejectionReason());
        dto.setEstimatedMileage(s.getEstimatedMileage());
        if (s.getRequester() != null) {
            dto.setRequesterRegistration(s.getRequester().getRegistration());
            dto.setRequesterName(s.getRequester().getFullName());
        }
        if (s.getVehicle() != null) {
            dto.setVehicleId(s.getVehicle().getId());
            dto.setVehiclePrefix(s.getVehicle().getPrefix());
            dto.setVehicleModel(s.getVehicle().getModel());
        }
        if (s.getServiceType() != null) {
            dto.setServiceTypeId(s.getServiceType().getId());
            dto.setServiceTypeName(s.getServiceType().getServiceName());
        }
        if (s.getApprover() != null) {
            dto.setApproverRegistration(s.getApprover().getRegistration());
            dto.setApproverName(s.getApprover().getFullName());
        }
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}