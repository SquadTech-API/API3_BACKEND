package br.com.edu.fatec.IPEMControl.DTO;

public class DadosVeiculoDashboardDTO {

    private Double gasto;
    private Double litros;
    private Double km;
    private Long saidas;
    private Double consumo;

    public DadosVeiculoDashboardDTO(Double gasto, Double litros, Double km, Long saidas, Double consumo) {
        this.gasto = gasto;
        this.litros = litros;
        this.km = km;
        this.saidas = saidas;
        this.consumo = consumo;
    }

    public Double getGasto() {
        return gasto;
    }

    public Double getLitros() {
        return litros;
    }

    public Double getKm() {
        return km;
    }

    public Long getSaidas() {
        return saidas;
    }

    public Double getConsumo() {
        return consumo;
    }
}