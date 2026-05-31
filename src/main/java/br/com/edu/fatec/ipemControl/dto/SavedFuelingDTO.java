package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SavedFuelingDTO {
    private Integer id;
    private LocalDateTime fuelingDatetime;
    private String fuelTypeName;
    private BigDecimal liters;
    private BigDecimal totalValue;
    private BigDecimal mileageAtFueling;
    private String stationName;
    private String stationCity;
    private String invoiceNumber;
    private Integer departureLogId;
}