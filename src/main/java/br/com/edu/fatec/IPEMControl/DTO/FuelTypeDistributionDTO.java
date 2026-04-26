package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FuelTypeDistributionDTO {
    private String fuelType;
    private Double percentage;
}