package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VeiculoResumoDTO {
    private Integer idVeiculo;
    private String modelo;
    private String prefixo;
    private String nucleoDar;
    private BigDecimal kmAtual;
    private LocalDateTime ultimoUso;
    private Boolean disponivel;
}


