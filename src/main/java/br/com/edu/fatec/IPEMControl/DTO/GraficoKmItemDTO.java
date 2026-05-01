package br.com.edu.fatec.IPEMControl.DTO;

public class GraficoKmItemDTO {

    private Integer id;
    private String prefix;
    private Double valor;

    public GraficoKmItemDTO(Integer id, String prefix, Double valor) {
        this.id = id;
        this.prefix = prefix;
        this.valor = valor;
    }

    public Integer getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public Double getValor() {
        return valor;
    }
}