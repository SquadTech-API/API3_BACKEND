package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReturnResponseDTO {

    private Integer departureId;
    private String status;

    private BigDecimal initialMileage;
    private BigDecimal finalMileage;
    private BigDecimal mileageDriven;

    private LocalDateTime departureDatetime;
    private LocalDateTime returnDatetime;

    private String vehicleModel;
    private String vehiclePrefix;
    private String userName;
}