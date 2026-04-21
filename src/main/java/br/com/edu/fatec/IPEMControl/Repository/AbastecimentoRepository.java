package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
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

    // ── Queries já existentes ────────────────────────────────────────────────

    Optional<Abastecimento> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Abastecimento> findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Abastecimento> findAllByOrderByDataHoraDesc();

    // ── Queries para relatórios ──────────────────────────────────────────────

    /**
     * Query 3.2: Custo por técnico no período.
     * Colunas: [0]=matricula_usuario, [1]=gasto_total, [2]=total_abast
     */
    @Query(value = """
        SELECT rs.matricula_usuario,
               COALESCE(SUM(a.valor_total), 0)     AS gasto_total,
               COUNT(a.id_abastecimento)            AS total_abast
        FROM registro_saida rs
        LEFT JOIN abastecimento a ON a.id_saida = rs.id_saida
        WHERE rs.data_hora_saida >= :dataInicio
        GROUP BY rs.matricula_usuario
        """, nativeQuery = true)
    List<Object[]> buscarCustoPorTecnico(@Param("dataInicio") LocalDateTime dataInicio);

    /** Gasto total do técnico no período */
    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    BigDecimal sumGastoPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                            @Param("dataInicio") LocalDateTime dataInicio);

    /** Total de abastecimentos do técnico no período */
    @Query(value = """
        SELECT COUNT(a.id_abastecimento)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida  >= :dataInicio
        """, nativeQuery = true)
    long countAbastPorMatriculaEPeriodo(@Param("matricula") Integer matricula,
                                        @Param("dataInicio") LocalDateTime dataInicio);

    /** Último abastecimento do técnico (para data do último abastecimento) */
    @Query(value = """
        SELECT a.*
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
        ORDER BY a.data_hora DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Abastecimento> findUltimoAbastecimentoDoTecnico(@Param("matricula") Integer matricula);
}