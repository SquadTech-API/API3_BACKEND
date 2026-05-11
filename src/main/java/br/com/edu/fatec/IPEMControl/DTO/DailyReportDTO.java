package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class DailyReportDTO {
    private Integer registration;
    private String technicianName;
    private LocalDate reportDate;
    private int totalActivities;
    private BigDecimal totalDailyMileage;
    private List<AtividadeDiariaDTO> activities;
}