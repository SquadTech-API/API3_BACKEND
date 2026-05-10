package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RetornoDTO {

    private BigDecimal kmFinal;
    private LocalDateTime dataRetorno;
    private String observacoes;
}