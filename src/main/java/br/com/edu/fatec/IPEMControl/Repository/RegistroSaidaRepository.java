package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroSaidaRepository extends JpaRepository<RegistroSaida, Integer> {

    // ── Queries já existentes ────────────────────────────────────────────────

    Optional<RegistroSaida> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer idVeiculo);

    Optional<RegistroSaida> findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(
            Integer idVeiculo, String status);

    Optional<RegistroSaida> findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(
            Integer matricula, String status);

    List<RegistroSaida> findByUsuarioMatriculaAndDataHoraSaidaBetween(
            Integer matricula, LocalDateTime inicioDia, LocalDateTime fimDia);

    // ── Queries para /relatorios/tecnicos/geral ──────────────────────────────

    /**
     * Query 3.1: Saídas e KM por técnico no período.
     * Colunas resultado: [0]=matricula, [1]=nome, [2]=total_saidas, [3]=km_total
     */
    @Query(value = """
        SELECT u.matricula, u.nome,
               COUNT(rs.id_saida)               AS total_saidas,
               COALESCE(SUM(rs.km_rodados), 0)  AS km_total
        FROM usuario u
        LEFT JOIN registro_saida rs
            ON rs.matricula_usuario = u.matricula
           AND rs.data_hora_saida  >= :dataInicio
        WHERE u.colaborador_ativo = TRUE
        GROUP BY u.matricula, u.nome
        ORDER BY total_saidas DESC
        """, nativeQuery = true)
    List<Object[]> buscarSaidasKmPorTecnico(@Param("dataInicio") LocalDateTime dataInicio);

    /**
     * KM médio por semana — últimas 4 semanas (para gráfico de linha).
     * Colunas resultado: [0]=semana (YEARWEEK), [1]=km_medio
     */
    @Query(value = """
        SELECT YEARWEEK(data_hora_saida, 1) AS semana,
               AVG(km_rodados)              AS km_medio
        FROM registro_saida
        WHERE data_hora_saida >= :dataInicio
          AND km_rodados IS NOT NULL
        GROUP BY semana
        ORDER BY semana DESC
        LIMIT 4
        """, nativeQuery = true)
    List<Object[]> buscarKmPorSemana(@Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Distribuição de serviços no período — para gráfico de rosca.
     * Colunas resultado: [0]=nome_servico, [1]=total
     */
    @Query(value = """
        SELECT ts.nome_servico, COUNT(rs.id_saida) AS total
        FROM registro_saida rs
        LEFT JOIN tipo_servico ts ON rs.id_tipo_servico = ts.id_tipo_servico
        WHERE rs.data_hora_saida >= :dataInicio
        GROUP BY ts.nome_servico
        """, nativeQuery = true)
    List<Object[]> buscarDistribuicaoServicos(@Param("dataInicio") LocalDateTime dataInicio);

    // ── Queries para /relatorios/tecnicos/{matricula} ────────────────────────

    /** Total de saídas do técnico no período */
    @Query(value = """
        SELECT COUNT(*) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    long countPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    /** Soma de km_rodados do técnico no período */
    @Query(value = """
        SELECT COALESCE(SUM(km_rodados), 0) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    BigDecimal sumKmPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                         @Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Query 3.5: Tempo médio de saída em horas (apenas saídas concluídas).
     */
    @Query(value = """
        SELECT AVG(TIMESTAMPDIFF(MINUTE, data_hora_saida, data_retorno)) / 60.0
        FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND status            = 'concluido'
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    Double calcularTempoMedioHoras(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    /** Maior km_rodados do técnico no período */
    @Query(value = """
        SELECT COALESCE(MAX(km_rodados), 0) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    BigDecimal buscarMaiorKm(@Param("matricula") Integer matricula,
                             @Param("dataInicio") LocalDateTime dataInicio);

    /** Duração em horas da saída mais longa (concluídas) */
    @Query(value = """
        SELECT COALESCE(MAX(TIMESTAMPDIFF(MINUTE, data_hora_saida, data_retorno)), 0) / 60.0
        FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND status            = 'concluido'
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    Double buscarMaiorDuracaoHoras(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Query 3.4: Destinos mais frequentes (top 5).
     * Colunas: [0]=local_destino, [1]=quantidade
     */
    @Query(value = """
        SELECT local_destino, COUNT(*) AS quantidade
        FROM registro_saida
        WHERE matricula_usuario = :matricula
        GROUP BY local_destino
        ORDER BY quantidade DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> buscarDestinosMaisFrequentes(@Param("matricula") Integer matricula);

    /**
     * Serviços realizados pelo técnico no período.
     * Colunas: [0]=nome_servico, [1]=total
     */
    @Query(value = """
        SELECT ts.nome_servico, COUNT(rs.id_saida) AS total
        FROM registro_saida rs
        LEFT JOIN tipo_servico ts ON rs.id_tipo_servico = ts.id_tipo_servico
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida  >= :dataInicio
        GROUP BY ts.nome_servico
        """, nativeQuery = true)
    List<Object[]> buscarServicosDoTecnico(@Param("matricula") Integer matricula,
                                           @Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Query 3.3: Saída ativa do técnico (em_andamento) — já existe equivalente,
     * mas reutilizamos findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc.
     */

    /** Última saída do técnico (qualquer status) */
    Optional<RegistroSaida> findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(Integer matricula);

    /**
     * Veículos distintos utilizados pelo técnico (prefixo + placa).
     */
    @Query(value = """
        SELECT DISTINCT CONCAT(v.prefixo, ' — ', v.placa)
        FROM registro_saida rs
        JOIN veiculo v ON rs.id_veiculo = v.id_veiculo
        WHERE rs.matricula_usuario = :matricula
        """, nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("matricula") Integer matricula);

    /** Contagem de técnicos ativos */
    @Query(value = "SELECT COUNT(*) FROM usuario WHERE colaborador_ativo = TRUE", nativeQuery = true)
    long countTecnicosAtivos();
}