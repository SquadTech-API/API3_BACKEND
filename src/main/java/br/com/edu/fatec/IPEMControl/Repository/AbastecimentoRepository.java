package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Fueling, Integer> {

    // NOVO MÉTODO: Essencial para o HistoricoUsoService (Item 11)
    @Query("SELECT a FROM Fueling a JOIN a.registroSaida rs WHERE rs.veiculo.idVeiculo = :idVeiculo")
    List<Fueling> findByVeiculoIdVeiculo(@Param("vehicleId") Integer idVeiculo);

    // Método essencial para o Relatório Uso Mensal
    List<Fueling> findByRegistroSaida(DepartureLog departureLog);

    Optional<Fueling> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Fueling> findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    List<Fueling> findAllByOrderByDataHoraDesc();

    @Query(value = """
        SELECT rs.matricula_usuario,
               COALESCE(SUM(a.valor_total), 0),
               COUNT(a.id_abastecimento)
        FROM registro_saida rs
        LEFT JOIN abastecimento a ON a.id_saida = rs.id_saida
        WHERE rs.data_hora_saida >= :dataInicio
        GROUP BY rs.matricula_usuario
        """, nativeQuery = true)
    List<Object[]> buscarCustoPorTecnico(@Param("startDatetime") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida >= :dataInicio
        """, nativeQuery = true)
    BigDecimal sumGastoPorMatriculaEPeriodo(@Param("registration") Integer matricula,
                                            @Param("startDatetime") LocalDateTime dataInicio);

    @Query(value = """
        SELECT COUNT(a.id_abastecimento)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
          AND rs.data_hora_saida >= :dataInicio
        """, nativeQuery = true)
    long countAbastPorMatriculaEPeriodo(@Param("registration") Integer matricula,
                                        @Param("startDatetime") LocalDateTime dataInicio);

    @Query(value = """
        SELECT a.*
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :matricula
        ORDER BY a.data_hora DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Fueling> findUltimoAbastecimentoDoTecnico(@Param("registration") Integer matricula);

    // ── Queries para dashboard de veículos ───────────────────────────────────

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalGastoSemana(@Param("vehicleId") Integer idVeiculo);

    @Query(value = """
        SELECT COALESCE(SUM(a.quantidade_litros), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :idVeiculo
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalLitrosSemana(@Param("vehicleId") Integer idVeiculo);

    // Filtros e Estatísticas
    List<Fueling> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    List<Fueling> findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(String placa);

    List<Fueling> findByDataHoraAfterOrderByDataHoraDesc(LocalDateTime inicio);

    @Query("SELECT WEEK(a.dateTime), SUM(a.totalValue), SUM(a.litersAmount) " +
            "FROM Fueling a WHERE a.dateTime >= :dataInicio " +
            "GROUP BY WEEK(a.dateTime) ORDER BY WEEK(a.dateTime)")
    List<Object[]> buscarEstatisticasSemanas(@Param("startDatetime") LocalDateTime dataInicio);

    @Query("SELECT a.gasStationName, a.gasStationCity, COUNT(a) " +
            "FROM Fueling a WHERE a.dateTime >= :dataInicio " +
            "GROUP BY a.gasStationName, a.gasStationCity " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarRankingPostos(@Param("startDatetime") LocalDateTime dataInicio);

    @Query("SELECT a.fuelType, COUNT(a) " +
            "FROM Fueling a WHERE a.dateTime >= :dataInicio " +
            "GROUP BY a.fuelType " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarDistribuicaoCombustivel(@Param("startDatetime") LocalDateTime dataInicio);

    @Query("SELECT v.placa, SUM(a.litersAmount), SUM(rs.kmRodados), " +
            "CASE WHEN SUM(a.litersAmount) > 0 " +
            "THEN SUM(rs.kmRodados) / SUM(a.litersAmount) ELSE 0 END, " +
            "SUM(a.totalValue) " +
            "FROM Fueling a " +
            "JOIN a.registroSaida rs " +
            "JOIN rs.veiculo v " +
            "WHERE a.dateTime >= :dataInicio " +
            "GROUP BY v.placa")
    List<Object[]> buscarConsumoPorVeiculo(@Param("startDatetime") LocalDateTime dataInicio);

    @Query("SELECT u.nome, COUNT(a), SUM(a.totalValue) " +
            "FROM Fueling a " +
            "JOIN a.registroSaida rs " +
            "JOIN rs.usuario u " +
            "WHERE a.dateTime >= :dataInicio " +
            "GROUP BY u.nome " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarRankingUsuarios(@Param("startDatetime") LocalDateTime dataInicio);
}