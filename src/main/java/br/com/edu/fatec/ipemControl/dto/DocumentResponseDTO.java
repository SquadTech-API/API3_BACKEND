package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {
    private Integer id;
    private String fileName;
    private String filePath;
    private Integer departureLogId;
    private LocalDateTime createdAt;
}