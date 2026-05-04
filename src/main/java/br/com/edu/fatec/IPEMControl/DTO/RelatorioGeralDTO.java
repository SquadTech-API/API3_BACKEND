package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resposta do GET /relatorios/tecnicos/geral?periodo={periodo}
 *
 * Cálculos removidos por serem responsabilidade do frontend:
 *   - kmMedioPorSaida  (kmTotal / totalSaidas)
 *   - custoPorSaida    (custoTotal / totalSaidas)
 */
@Data
public class RelatorioGeralDTO {

    private ResumoGeral resumo;
    private List<TecnicoResumoDTO> tecnicos;

    @Data
    public static class ResumoGeral {

        private Long totalSaidas;

        private Long tecnicosAtivos;

        private BigDecimal kmTotal;

        private BigDecimal custoTotal;

        private List<Long> saidasPorTecnico;

        private List<BigDecimal> kmPorTecnico;

        private List<BigDecimal> gastoPorTecnico;

        private List<BigDecimal> kmPorSemana;
    }
}