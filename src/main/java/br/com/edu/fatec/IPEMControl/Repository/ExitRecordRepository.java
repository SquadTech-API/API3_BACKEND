package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExitRecordRepository extends JpaRepository<DepartureLog, Integer> {

    // Relatório de uso mensal
    List<DepartureLog> findByDataRetornoBetween(LocalDateTime start, LocalDateTime end);

    List<DepartureLog> findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
            Integer vehicleId, LocalDateTime start, LocalDateTime end);

    List<DepartureLog> findByUsuarioMatriculaAndDataHoraSaidaBetween(
            Integer registration, LocalDateTime start, LocalDateTime end);

    // Controle de fluxo
    Optional<DepartureLog> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer vehicleId);
    Optional<DepartureLog> findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(Integer registration, String status);
    Optional<DepartureLog> findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(Integer vehicleId, String status);
    Optional<DepartureLog> findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(Integer registration);

    List<DepartureLog> findByStatus(String status);

    // Dashboard veículos
    @Query(value = "SELECT v.id_veiculo, v.modelo, SUM(rs.km_rodados) as total_km " +
            "FROM registro_saida rs JOIN veiculo v ON v.id_veiculo = rs.id_veiculo " +
            "WHERE rs.data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY v.id_veiculo, v.modelo ORDER BY total_km DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarTop5KmSemana();

    @Query(value = "SELECT COALESCE(SUM(km_rodados), 0.0) FROM registro_saida " +
            "WHERE id_veiculo = :vehicleId AND data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
    Double totalKmSemana(@Param("vehicleId") Integer vehicleId);

    @Query(value = "SELECT COUNT(*) FROM registro_saida " +
            "WHERE id_veiculo = :vehicleId AND data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
    Long totalSaidasSemana(@Param("vehicleId") Integer vehicleId);

    // Relatório técnico
    @Query(value = "SELECT rs.matricula_usuario, u.nome, COUNT(rs.id_saida), COALESCE(SUM(rs.km_rodados), 0) " +
            "FROM registro_saida rs JOIN usuario u ON u.matricula = rs.matricula_usuario " +
            "WHERE rs.data_hora_saida >= :startDate GROUP BY rs.matricula_usuario, u.nome", nativeQuery = true)
    List<Object[]> buscarSaidasKmPorTecnico(@Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT rs.matricula_usuario, rs.km_rodados FROM registro_saida rs WHERE rs.data_hora_saida >= :startDate", nativeQuery = true)
    List<Object[]> buscarKmPorSemana(@Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT COUNT(*) FROM registro_saida WHERE matricula_usuario = :registration AND data_hora_saida >= :startDate", nativeQuery = true)
    long countPorMatriculaEPeriodo(@Param("registration") Integer registration, @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT COALESCE(SUM(km_rodados), 0) FROM registro_saida WHERE matricula_usuario = :registration AND data_hora_saida >= :startDate", nativeQuery = true)
    BigDecimal sumKmPorMatriculaEPeriodo(@Param("registration") Integer registration, @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT MAX(km_rodados) FROM registro_saida WHERE matricula_usuario = :registration AND data_hora_saida >= :startDate", nativeQuery = true)
    BigDecimal buscarMaiorKm(@Param("registration") Integer registration, @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT MAX(TIMESTAMPDIFF(SECOND, data_hora_saida, data_retorno)) / 3600 FROM registro_saida WHERE matricula_usuario = :registration AND data_hora_saida >= :startDate AND status = 'concluido'", nativeQuery = true)
    Double buscarMaiorDuracaoHoras(@Param("registration") Integer registration, @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT COALESCE(AVG(TIMESTAMPDIFF(SECOND, data_hora_saida, data_retorno)) / 3600, 0) FROM registro_saida WHERE matricula_usuario = :registration AND data_hora_saida >= :startDate AND status = 'concluido'", nativeQuery = true)
    Double calcularTempoMedioHoras(@Param("registration") Integer registration, @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT local_destino, COUNT(*) as freq FROM registro_saida WHERE matricula_usuario = :registration GROUP BY local_destino ORDER BY freq DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarDestinosMaisFrequentes(@Param("registration") Integer registration);

    // CORRIGIDO: era "ts.name" — coluna correta é "nome_servico"
    @Query(value = "SELECT ts.nome_servico, COUNT(rs.id_saida) FROM registro_saida rs " +
            "JOIN tipo_servico ts ON ts.id_tipo_servico = rs.id_tipo_servico " +
            "WHERE rs.matricula_usuario = :registration AND rs.data_hora_saida >= :startDate " +
            "GROUP BY ts.nome_servico", nativeQuery = true)
    List<Object[]> buscarServicosDoTecnico(@Param("registration") Integer registration,
                                           @Param("startDatetime") LocalDateTime startDate);

    @Query(value = "SELECT DISTINCT CONCAT(v.modelo, ' (', v.prefixo, ')') FROM registro_saida rs " +
            "JOIN veiculo v ON v.id_veiculo = rs.id_veiculo WHERE rs.matricula_usuario = :registration", nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("registration") Integer registration);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE colaborador_ativo = true", nativeQuery = true)
    long countTecnicosAtivos();

    // NOVO: busca saídas para relatório de troca de óleo de veículo
    @Query(value = "SELECT rs.* FROM registro_saida rs " +
            "JOIN tipo_servico ts ON ts.id_tipo_servico = rs.id_tipo_servico " +
            "WHERE rs.id_veiculo = :vehicleId AND ts.eh_troca_oleo = true " +
            "ORDER BY rs.data_hora_saida DESC", nativeQuery = true)
    List<DepartureLog> findByVeiculoIdVeiculoAndTipoServicoEhTrocaOleoTrue(@Param("vehicleId") Integer vehicleId);
}