package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class VehicleConsumptionDTO {
    private String plate;
    private BigDecimal currentOdometer;
    private BigDecimal nextOilChangeOdometer;
    private BigDecimal lastOilChangeOdometer;
    private String lastOilChangeDate;
    private Double consumptionKmL;
    private Double costPerKm;
    private BigDecimal totalSpent;
    private BigDecimal totalLiters;
}
