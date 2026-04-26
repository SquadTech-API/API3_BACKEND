package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FuelSupplyDTO {
    private Integer exitRecordId;
    private LocalDateTime dateTime;
    private String fuelType;
    private BigDecimal litersAmount;
    private BigDecimal totalValue;
    private BigDecimal odometerReading;
    private String stationName;
    private String stationCity;
    private String invoiceNumber;
}
