package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class VehicleReportDTO {

    private String prefix;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private String fuelType;
    private Double mileageDriven;
    private Double avgConsumption;
    private Integer totalDepartures;
    }