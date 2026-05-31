package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReturnDTO {

    private BigDecimal finalMileage;
    private LocalDateTime returnDatetime;
    private String observations;
}