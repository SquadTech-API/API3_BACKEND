package br.com.edu.fatec.IPEMControl.DTO;

import java.util.Map;

public class VeiculoDashboardDTO {

    private Integer id;
    private String modelo;
    private String prefixo;
    private Map<String, DadosVeiculoDashboardDTO> dados;
    private ManutencaoVeiculoDTO manutencao;

    public VeiculoDashboardDTO(Integer id, String modelo, String prefixo,
                               Map<String, DadosVeiculoDashboardDTO> dados,
                               ManutencaoVeiculoDTO manutencao) {
        this.id = id;
        this.modelo = modelo;
        this.prefixo = prefixo;
        this.dados = dados;
        this.manutencao = manutencao;
    }

    public Integer getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public Map<String, DadosVeiculoDashboardDTO> getDados() {
        return dados;
    }

    public ManutencaoVeiculoDTO getManutencao() {
        return manutencao;
    }
}