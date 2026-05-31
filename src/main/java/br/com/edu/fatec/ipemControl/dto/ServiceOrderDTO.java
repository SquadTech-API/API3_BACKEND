package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;

@Data
public class ServiceOrderDTO {
    private Integer vehicleId;
    private Integer serviceTypeId;
    private String observations;
}