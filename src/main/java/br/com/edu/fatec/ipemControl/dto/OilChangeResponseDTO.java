package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OilChangeResponseDTO {
    private Integer oilChangeId;
    private BigDecimal changeKm;
    private BigDecimal intervalKm;
    private BigDecimal nextChangeKm;
    private LocalDate changeDate;
    private String observation;
    private Boolean alertSent;
    private Integer departureLogId;
    private Integer vehicleId;
    private String vehiclePrefix;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
