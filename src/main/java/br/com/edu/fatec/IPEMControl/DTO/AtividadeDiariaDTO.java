package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AtividadeDiariaDTO {
    private String veiculoPrefixo;
    private String localDestino;
    private LocalDateTime horaSaida;
    private LocalDateTime horaRetorno;
    private BigDecimal kmRodados;
    private String status;
}