package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VeiculoResumoDTO {

    // Jackson serializa como "idVeiculo" (camelCase padrão)
    // veiculos.js lê v.idVeiculo para salvar no sessionStorage
    private Integer idVeiculo;

    private String modelo;
    private String prefixo;
    private String ultimoUso;
    private String ultimoAbastecimento;
    private String km;
    private String status;
}