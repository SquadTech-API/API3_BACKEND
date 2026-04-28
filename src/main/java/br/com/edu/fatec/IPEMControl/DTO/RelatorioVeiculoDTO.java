package br.com.edu.fatec.IPEMControl.DTO;

public class RelatorioVeiculoDTO {

    private String prefixo;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private String combustivel;
    private Double kmRodado;
    private Double consumoMedio;
    private Integer totalSaidas;

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public Double getKmRodado() {
        return kmRodado;
    }

    public void setKmRodado(Double kmRodado) {
        this.kmRodado = kmRodado;
    }

    public Double getConsumoMedio() {
        return consumoMedio;
    }

    public void setConsumoMedio(Double consumoMedio) {
        this.consumoMedio = consumoMedio;
    }

    public Integer getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(Integer totalSaidas) {
        this.totalSaidas = totalSaidas;
    }
}