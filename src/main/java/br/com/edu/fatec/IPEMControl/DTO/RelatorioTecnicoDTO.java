package br.com.edu.fatec.IPEMControl.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Relatório individual completo de um técnico.
 * Resposta do GET /relatorios/tecnicos/{matricula}?periodo={periodo}
 *
 * Removidos por serem responsabilidade do frontend:
 *   - alertas         (derivado dos dados já presentes no response)
 *   - eficiencia      (kmTotal / gastoTotal)
 *   - mediaMensal     (saidas / 4)
 *   - taxaLeitura     ((lidos / recebidos) * 100)
 */
@Data
public class RelatorioTecnicoDTO {

    // ── Seção 1: Identificação ────────────────────────────────────────────────
    private Integer matricula;
    private String nome;
    private String cargo;
    private String tipo;
    private String cnh;
    private String numHabilitacao;
    private String cpf;
    private String email;
    private String telefone;
    /** ISO 8601: YYYY-MM-DD */
    private String dataNascimento;

    // ── Seção 2: Status operacional ──────────────────────────────────────────
    private Boolean ativo;
    /** ISO 8601: YYYY-MM-DDTHH:mm:ss */
    private String dataCadastro;
    /** ISO 8601: YYYY-MM-DDTHH:mm:ss */
    private String ultimaAtualizacao;
    private Boolean saidaEmAberto;
    private Integer idSaidaAberta;

    // ── Seção 3: Uso do sistema ───────────────────────────────────────────────
    /** Map<"hoje"|"7"|"30"|"ano", total de saídas> */
    private Map<String, Long> saidasPorPeriodo;
    /** Map<"hoje"|"7"|"30"|"ano", km total> */
    private Map<String, BigDecimal> kmPorPeriodo;
    private String ultimaSaidaData;
    private String ultimaSaidaVeiculo;
    private String ultimaSaidaDestino;

    // ── Seção 4: Comportamento operacional ───────────────────────────────────
    private Double tempoMedioSaidaHoras;
    private BigDecimal maiorSaidaKm;
    private Double maiorSaidaDuracaoHoras;
    private Double frequenciaSaidasPorSemana;

    // ── Seção 5: Responsabilidade financeira ─────────────────────────────────
    /** Map<"hoje"|"7"|"30"|"ano", gasto total> */
    private Map<String, BigDecimal> gastoPorPeriodo;
    /** Map<"hoje"|"7"|"30"|"ano", total de abastecimentos> */
    private Map<String, Long> abastPorPeriodo;

    // ── Seção 6: Manutenção ──────────────────────────────────────────────────
    private Long trocasOleo;
    private String ultimaTrocaOleo;
    private List<String> veiculosUtilizados;

    // ── Seção 7: Documentos e compliance ─────────────────────────────────────
    private DocumentosDTO documentos;

    // ── Destinos e serviços ───────────────────────────────────────────────────
    private List<DestinoFrequenteDTO> destinos;
    /** Map<nomeServico, count> */
    private Map<String, Long> servicos;

    // ─────────────────────────────────────────────────────────────────────────

    @Data
    public static class DocumentosDTO {
        private Long recebidos;
        private Long lidos;
        private Long baixados;
        // taxaLeitura removida — frontend calcula: (lidos / recebidos) * 100
    }
}