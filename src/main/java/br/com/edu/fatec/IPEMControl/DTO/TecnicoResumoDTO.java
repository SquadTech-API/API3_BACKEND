package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TecnicoResumoDTO {
    private Integer matricula;
    private String nome;
    private Long saidasPorPeriodo;
    private BigDecimal kmPorPeriodo;
}