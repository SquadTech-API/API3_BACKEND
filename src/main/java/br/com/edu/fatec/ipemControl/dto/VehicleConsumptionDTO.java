package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class VehicleConsumptionDTO {
    private String plate;
    private BigDecimal currentMileage;
    private BigDecimal nextOilChangeMileage;
    private BigDecimal lastOilChangeMileage;
    private String lastOilChangeDate;
    private Double fuelConsumptionKmPerLiter;
    private Double costPerKilometer;
    private BigDecimal totalCost;
    private BigDecimal totalLiters;
}
