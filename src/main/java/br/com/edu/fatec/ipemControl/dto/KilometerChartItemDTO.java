package br.com.edu.fatec.ipemControl.dto;

public class KilometerChartItemDTO {

    private Integer id;
    private String prefix;
    private Double value;

    public KilometerChartItemDTO(
            Integer id,
            String prefix,
            Double value
    ) {
        this.id = id;
        this.prefix = prefix;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getPrefix() {
        return prefix;
    }

    public Double getValue() {
        return value;
    }
}