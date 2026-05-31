package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OilChangeDTO {
    private Integer vehicleId;
    private Integer departureLogId;
    private BigDecimal changeMileage;
    private BigDecimal intervalKm;
    private LocalDate changeDate;
    private String notes;
}