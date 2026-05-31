package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FuelingDTO {
    private Integer tripId;
    private LocalDateTime dateTime;
    private String fuelType;
    private BigDecimal litersQuantity;
    private BigDecimal totalAmount;
    private BigDecimal fuelingMileage;
    private String gasStationName;
    private String gasStationCity;
    private String invoiceNumber;
}