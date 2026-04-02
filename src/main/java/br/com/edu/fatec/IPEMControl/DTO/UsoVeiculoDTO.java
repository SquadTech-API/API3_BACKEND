package br.com.edu.fatec.IPEMControl.DTO;

import java.time.LocalDateTime;

public class UsoVeiculoDTO {

    private Long tecnicoId;
    private String veiculo;
    private LocalDateTime dataInicio;

    // getters e setters

    public Long getTecnicoId() { return tecnicoId; }
    public void setTecnicoId(Long motoristaId) { this.tecnicoId = tecnicoId; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
}