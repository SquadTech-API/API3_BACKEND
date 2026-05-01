package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistribuicaoCombustivelDTO {
    private String tipo;
    private Double pct;
}