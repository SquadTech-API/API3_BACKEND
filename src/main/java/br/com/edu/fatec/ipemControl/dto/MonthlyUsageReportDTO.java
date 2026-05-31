package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsageReportDTO {
    private BigDecimal totalMileage;
    private Integer totalTrips;
    private BigDecimal totalSpending;
    private BigDecimal totalLiters;
    private List<DepartureLogResponseDTO> details;
}