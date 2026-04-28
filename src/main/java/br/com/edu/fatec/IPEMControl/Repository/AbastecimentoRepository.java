package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
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
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Integer> {

    // Método essencial para o Relatório Uso Mensal
    List<Abastecimento> findByRegistroSaida(RegistroSaida registroSaida);

    Optional<Abastecimento> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Abastecimento> findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Abastecimento> findAllByOrderByDataHoraDesc();

    @Query(value = """
        SELECT rs.matricula_usuario,
               COALESCE(SUM(a.valor_total), 0),
               COUNT(a.id_abastecimento)
        FROM registro_saida rs
        LEFT JOIN abastecimento a ON a.id_saida = rs.id_saida
        WHERE rs.data_hora_saida >= :dataInicio
        GROUP BY rs.matricula_usuario
        """, nativeQuery = true)
    List<Object[]> buscarCustoPorTecnico(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida >= :dataInicio
        """, nativeQuery = true)
    BigDecimal sumGastoPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                            @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COUNT(a.id_abastecimento)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida >= :dataInicio
        """, nativeQuery = true)
    long countAbastPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                        @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = """
        SELECT a.*
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
        ORDER BY a.data_hora DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Abastecimento> findUltimoAbastecimentoDoTecnico(@Param("matricula") Integer matricula);

    // ── Queries para dashboard de veículos ───────────────────────────────────

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalGastoSemana(@Param("idVeiculo") Integer idVeiculo);

    @Query(value = """
        SELECT COALESCE(SUM(a.quantidade_litros), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalLitrosSemana(@Param("idVeiculo") Integer idVeiculo);
}