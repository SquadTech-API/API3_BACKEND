package br.com.edu.fatec.IPEMControl.DTO;

import java.time.LocalDateTime;

public class HistoricoUsoDTO {

    private String descricao;
    private LocalDateTime dataRegistro;

    public HistoricoUsoDTO() {
    }

    public HistoricoUsoDTO(String descricao, LocalDateTime dataRegistro) {
        this.descricao = descricao;
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }
}