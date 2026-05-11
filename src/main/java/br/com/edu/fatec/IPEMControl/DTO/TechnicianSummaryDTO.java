package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TechnicianSummaryDTO {
    private Integer registration;
    private String name;
    private Long departuresByPeriod;
    private BigDecimal kmByPeriod;
}