package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Integer id;
    private String type;
    private String title;
    private String message;
    private String reason;
    private Integer referenceId;
    private Boolean read;
    private LocalDateTime createdAt;
}