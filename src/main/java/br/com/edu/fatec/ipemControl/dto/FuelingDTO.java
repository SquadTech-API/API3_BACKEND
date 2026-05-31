package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FuelingDTO {
    private Integer departureLogId;
    private Integer fuelTypeId;
    private LocalDateTime fuelingDatetime;
    private BigDecimal liters;
    private BigDecimal totalValue;
    private BigDecimal mileageAtFueling;
    private String stationName;
    private String stationCity;
    private String invoiceNumber;
    private String receiptUrl;
}