package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepartureLogResponseDTO {
    private Integer departureLogId;
    private String destination;
    private String status;
    private String observations;
    private LocalDateTime departureDateTime;
    private LocalDateTime returnDate;
    private BigDecimal startingKm;
    private BigDecimal finishingKm;
    private BigDecimal drivenKm;
    private Integer vehicleId;
    private String vehiclePrefix;
    private String vehicleModel;
    private String vehicleLicensePlate;
    private Integer userRegistration;
    private String userName;
    private Integer serviceTypeId;
    private String serviceName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
