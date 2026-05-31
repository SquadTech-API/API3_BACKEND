package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FuelTypeDTO {
    private Integer id;
    private String name;
    private String abbreviation;
    private String category;
    private BigDecimal pricePerLiter;
    private Boolean active;
}