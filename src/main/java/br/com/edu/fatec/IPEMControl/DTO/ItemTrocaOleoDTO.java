package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemTrocaOleoDTO {
    private LocalDateTime dataTroca;
    private String placaVeiculo;
    private BigDecimal kmNaTroca;
    private BigDecimal kmProximaTroca;
    private BigDecimal kmAtual;
}