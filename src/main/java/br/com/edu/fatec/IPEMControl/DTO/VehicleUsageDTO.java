package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class VehicleUsageDTO {

    private Long technicianId;
    private String vehicle;
    private LocalDateTime startDatetime;

}