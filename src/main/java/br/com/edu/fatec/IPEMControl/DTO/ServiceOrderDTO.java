package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

@Data
public class ServiceOrderDTO {
    private Integer vehicleId;
    private Integer serviceTypeId;
    private String observations;
}