package br.com.edu.fatec.IPEMControl.DTO;

import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyUsageReportDTO {

    private BigDecimal totalMileage;
    private Integer totalTrips;
    private BigDecimal totalSpending;   // Adicionado para o R$ do Dashboard
    private BigDecimal totalLiters;  // Adicionado para o L do Dashboard
    private List<DepartureLog> details;
    }
