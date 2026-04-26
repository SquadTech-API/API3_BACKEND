package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FuelSupplySaveDTO {
    private Integer fuelSupplyId;
    private LocalDateTime dateTime;
    private String fuelType;
    private BigDecimal litersAmount;
    private BigDecimal totalValue;
    private BigDecimal odometerReading;
    private String stationName;
    private String stationCity;
    private String invoiceNumber;
    private Integer exitRecordId;
}