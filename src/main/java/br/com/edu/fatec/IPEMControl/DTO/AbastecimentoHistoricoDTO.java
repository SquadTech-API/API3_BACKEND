package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AbastecimentoHistoricoDTO {
    private Integer idAbastecimento;
    private LocalDateTime dataHora;
    private String tipoCombustivel;
    private BigDecimal quantidadeLitros;
    private BigDecimal valorTotal;
    private BigDecimal kmAbastecimento;
    private String postoNome;
    private String postoCidade;
    private String notaFiscal;
    private Integer idVeiculo;
    private String modeloVeiculo;
    private String prefixoVeiculo;
    private String placaVeiculo;
    private String nomeResponsavel;
}