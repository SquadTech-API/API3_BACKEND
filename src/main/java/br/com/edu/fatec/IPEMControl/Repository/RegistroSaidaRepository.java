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

    @Query(value = """
        SELECT ts.nome_servico, COUNT(rs.id_saida) AS total
        FROM registro_saida rs
        LEFT JOIN tipo_servico ts ON rs.id_tipo_servico = ts.id_tipo_servico
        WHERE rs.data_hora_saida >= :dataInicio
        GROUP BY ts.nome_servico
        """, nativeQuery = true)
    List<Object[]> buscarDistribuicaoServicos(@Param("dataInicio") LocalDateTime dataInicio);

    // ── Queries para /relatorios/tecnicos/{matricula} ────────────────────────

    @Query(value = """
        SELECT COUNT(*) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    long countPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COALESCE(SUM(km_rodados), 0) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    BigDecimal sumKmPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                         @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT AVG(TIMESTAMPDIFF(MINUTE, data_hora_saida, data_retorno)) / 60.0
        FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND status            = 'concluido'
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    Double calcularTempoMedioHoras(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COALESCE(MAX(km_rodados), 0) FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    BigDecimal buscarMaiorKm(@Param("matricula") Integer matricula,
                             @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COALESCE(MAX(TIMESTAMPDIFF(MINUTE, data_hora_saida, data_retorno)), 0) / 60.0
        FROM registro_saida
        WHERE matricula_usuario = :matricula
          AND status            = 'concluido'
          AND data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    Double buscarMaiorDuracaoHoras(@Param("matricula") Integer matricula,
                                   @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT local_destino, COUNT(*) AS quantidade
        FROM registro_saida
        WHERE matricula_usuario = :matricula
        GROUP BY local_destino
        ORDER BY quantidade DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> buscarDestinosMaisFrequentes(@Param("matricula") Integer matricula);

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

    Optional<RegistroSaida> findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(Integer matricula);

    @Query(value = """
        SELECT DISTINCT CONCAT(v.prefixo, ' — ', v.placa)
        FROM registro_saida rs
        JOIN veiculo v ON rs.id_veiculo = v.id_veiculo
        WHERE rs.matricula_usuario = :matricula
        """, nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("matricula") Integer matricula);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE colaborador_ativo = TRUE", nativeQuery = true)
    long countTecnicosAtivos();

    // ── Queries para dashboard de veículos ───────────────────────────────────

    @Query(value = """
        SELECT v.id_veiculo,
               v.prefixo,
               COALESCE(SUM(rs.km_rodados), 0) AS km_total
        FROM registro_saida rs
        JOIN veiculo v ON rs.id_veiculo = v.id_veiculo
        WHERE rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        GROUP BY v.id_veiculo, v.prefixo
        ORDER BY km_total DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> buscarTop5KmSemana();

    @Query(value = """
        SELECT COALESCE(SUM(rs.km_rodados), 0)
        FROM registro_saida rs
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalKmSemana(@Param("idVeiculo") Integer idVeiculo);

    @Query(value = """
        SELECT COUNT(*)
        FROM registro_saida rs
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Long totalSaidasSemana(@Param("idVeiculo") Integer idVeiculo);
}