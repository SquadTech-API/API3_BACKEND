package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepartureLogDTO {
    private Integer vehicleId;
    private Integer userRegistration;
    private Integer serviceTypeId;
    private String destination;
    private String observations;
    private BigDecimal initialMileage;
    private LocalDateTime departureDatetime;
}