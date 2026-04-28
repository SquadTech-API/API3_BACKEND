package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HistoricoUsoCardDTO {
    private String motorista;       // Nome do motorista aqui
    private LocalDateTime dataSaida;
    private String tipoServico;
    private BigDecimal kmRodados;
    private boolean abasteceu;      // Destacado em verde se for true
}