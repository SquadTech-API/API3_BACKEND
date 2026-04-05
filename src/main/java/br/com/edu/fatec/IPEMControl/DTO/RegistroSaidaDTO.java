package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegistroSaidaDTO {
    private Integer idVeiculo;
    private Integer matriculaUsuario;
    private Integer idTipoServico;
    private String localDestino;
    private String observacoes;
    private BigDecimal kmInicial;
    private LocalDateTime dataHoraSaida;
}