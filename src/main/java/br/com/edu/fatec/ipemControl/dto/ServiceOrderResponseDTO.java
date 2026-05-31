package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ServiceOrderResponseDTO {
    private Integer serviceOrderId;
    private String vehicleLicensePlate;
    private String vehicleModel;
    private String serviceName;
    private String status;
    private LocalDateTime openingDate;
    private String observations;
}