package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RetornoRespostaDTO {

    private Integer idSaida;
    private String status;

    private BigDecimal kmInicial;
    private BigDecimal kmFinal;
    private BigDecimal kmRodados;

    private LocalDateTime dataHoraSaida;
    private LocalDateTime dataRetorno;

    private String modeloVeiculo;
    private String prefixoVeiculo;
    private String nomeUsuario;
}