package br.com.edu.fatec.IPEMControl.DTO;

public class ManutencaoVeiculoDTO {

    private Double kmAtual;
    private Double proximaTroca;

    public ManutencaoVeiculoDTO(Double kmAtual, Double proximaTroca) {
        this.kmAtual = kmAtual;
        this.proximaTroca = proximaTroca;
    }

    public Double getKmAtual() {
        return kmAtual;
    }

    public Double getProximaTroca() {
        return proximaTroca;
    }
}