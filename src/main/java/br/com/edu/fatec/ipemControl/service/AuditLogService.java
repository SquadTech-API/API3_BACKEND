package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.AuditLogResponseDTO;
import br.com.edu.fatec.ipemControl.entity.AuditLog;
import br.com.edu.fatec.ipemControl.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // ── GET /audit-logs — paginado com filtros (#A16) ─────────────
    public Page<AuditLogResponseDTO> findWithFilters(
            Integer registration,
            String entity,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        return auditLogRepository
                .findWithFilters(registration, entity, action, from, to, pageable)
                .map(this::toDTO);
    }

    // ── Registro interno (chamado pelos services ao editar) ───────
    public void log(Integer userRegistration, String userName,
                    String entity, Integer entityId,
                    String action, String description) {

        AuditLog log = AuditLog.builder()
                .userRegistration(userRegistration)
                .userName(userName)
                .entity(entity)
                .entityId(entityId)
                .action(action)
                .description(description)
                .build();

        auditLogRepository.save(log);
    }

    // ── Mapeamento ────────────────────────────────────────────────
    private AuditLogResponseDTO toDTO(AuditLog a) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(a.getId());
        dto.setUserRegistration(a.getUserRegistration());
        dto.setUserName(a.getUserName());
        dto.setEntity(a.getEntity());
        dto.setEntityId(a.getEntityId());
        dto.setAction(a.getAction());
        dto.setDescription(a.getDescription());
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }
}