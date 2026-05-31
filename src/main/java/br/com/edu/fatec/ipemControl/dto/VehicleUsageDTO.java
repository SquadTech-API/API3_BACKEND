package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VehicleUsageDTO {

    private Long technicianId;
    private String vehicle;
    private LocalDateTime startDatetime;

}