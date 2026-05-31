package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AuditLogResponseDTO {
    private Integer id;
    private Integer userRegistration;
    private String userName;
    private String entity;
    private Integer entityId;
    private String action;
    private String description;
    private LocalDateTime createdAt;
}