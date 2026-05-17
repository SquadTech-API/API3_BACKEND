package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FuelDistributionDTO {

    private String type;
    private Double percentage;
}