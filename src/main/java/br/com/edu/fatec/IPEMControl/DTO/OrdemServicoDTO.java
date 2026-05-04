package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class OrdemServicoDTO {
    private Integer idVeiculo;
    private Integer idTipoServico;
    private String observacoes;
}