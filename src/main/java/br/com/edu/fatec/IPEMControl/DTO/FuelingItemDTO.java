package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FuelingItemDTO {
    private LocalDateTime dateTime;
    private String vehicle;
    private String responsiblePerson;
    private String fuelType;
    private BigDecimal litersQuantity;
    private BigDecimal totalAmount;
    private BigDecimal fuelingMileage;
    private String gasStationName;
    private String gasStationCity;
    private String invoiceNumber;
}