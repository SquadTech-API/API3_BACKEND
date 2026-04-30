package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ConsumoVeiculoDTO {
    private String placa;
    private BigDecimal kmAtual;
    private BigDecimal kmProximaTroca;
    private BigDecimal kmUltimaTroca;
    private String dataUltimaTroca;
    private Double consumoKmL;
    private Double custoPorKm;
    private BigDecimal totalGasto;
    private BigDecimal totalLitros;
}