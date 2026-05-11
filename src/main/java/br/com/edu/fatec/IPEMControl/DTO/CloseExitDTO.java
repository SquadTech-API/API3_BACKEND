package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CloseExitDTO {

    private BigDecimal finalMileage;
    private LocalDateTime returnDate;
    private String observations;
}