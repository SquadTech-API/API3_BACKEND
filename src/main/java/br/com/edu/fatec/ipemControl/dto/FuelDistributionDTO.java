package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FuelDistributionDTO {

    private String type;
    private Double percentage;
}