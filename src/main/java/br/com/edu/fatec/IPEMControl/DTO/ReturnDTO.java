package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReturnDTO {

    private BigDecimal finalMileage;
    private LocalDateTime returnDatetime;
    private String observations;
}