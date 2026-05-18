package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehicleDTO {
    private Integer vehicleId;
    private String prefix;
    private String darCenter;
    private String licensePlate;
    private String model;
    private String brand;
    private Integer year;
    private String fuelType;
    private String licenseCategory;
    private BigDecimal currentKm;
    private Boolean available;
    private Boolean active;
    private String flNumber;
    private BigDecimal oilChangeIntervalKm;
    private Boolean oilChangeAlertSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
