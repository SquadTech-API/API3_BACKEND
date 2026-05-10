package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrdemServicoRespostaDTO {
    private Integer idOrdemServico;
    private String placaVeiculo;
    private String modeloVeiculo;
    private String nomeServico;
    private String status;
    private LocalDateTime dataAbertura;
    private String observacoes;
}