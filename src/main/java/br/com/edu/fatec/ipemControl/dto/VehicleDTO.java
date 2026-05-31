package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VehicleDTO {
    private Integer id;
    private String prefix;
    private String darCenter;
    private String licensePlate;
    private String model;
    private String brand;
    private Integer manufactureYear;
    private Integer fuelTypeId;
    private String fuelTypeName;
    private String licenseCategory;
    private BigDecimal currentMileage;
    private Boolean available;
    private Boolean active;
    private String flNumber;
    private BigDecimal oilChangeIntervalKm;
    private BigDecimal nextOilChangeMileage;
    private Boolean oilChangeAlertSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}