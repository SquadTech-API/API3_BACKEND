package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta do GET /relatorios/technicians/geral?periodo={periodo}
 *
 * Cálculos removidos por serem responsabilidade do frontend:
 *   - kmMedioPorSaida  (totalKm / totalDepartures)
 *   - custoPorSaida    (totalCost / totalDepartures)
 */
@Data
public class GeneralReportDTO {

    private GeneralSummary summary;
    private List<TechnicianSummaryDTO> technicians;

    @Data
    public static class GeneralSummary {

        private Long totalDepartures;

        private Long activeTechnicians;

        private BigDecimal totalKm;

        private BigDecimal totalCost;

        private List<Long> departuresPerTechnician;

        private List<BigDecimal> kmPerTechnician;

        private List<BigDecimal> spendingPerTechnician;

        private List<BigDecimal> kmPerWeek;
    }
}