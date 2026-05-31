package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDocumentResponseDTO {

    // ── Campos de vínculo individual ──────────────────────────────
    private Integer id;
    private Boolean isRead;
    private Boolean downloaded;
    private LocalDateTime accessedAt;
    private Integer userRegistration;
    private Integer documentId;

    // ── Campos de estatísticas (/documents/user/{registration}/stats)
    private Long received;
    private Long readCount;
    private Long downloadedCount;
}