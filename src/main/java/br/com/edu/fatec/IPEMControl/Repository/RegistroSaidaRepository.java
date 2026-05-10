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
public interface RegistroSaidaRepository extends JpaRepository<DepartureLog, Integer> {

    // Relatório de uso mensal
    List<DepartureLog> findByDataRetornoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<DepartureLog> findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
            Integer idVeiculo, LocalDateTime inicio, LocalDateTime fim);

    List<DepartureLog> findByUsuarioMatriculaAndDataHoraSaidaBetween(
            Integer matricula, LocalDateTime inicio, LocalDateTime fim);

    // Controle de fluxo
    Optional<DepartureLog> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer idVeiculo);
    Optional<DepartureLog> findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(Integer matricula, String status);
    Optional<DepartureLog> findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(Integer idVeiculo, String status);
    Optional<DepartureLog> findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(Integer matricula);

    List<DepartureLog> findByStatus(String status);

    // Dashboard veículos
    @Query(value = "SELECT v.id_veiculo, v.modelo, SUM(rs.km_rodados) as total_km " +
            "FROM registro_saida rs JOIN veiculo v ON v.id_veiculo = rs.id_veiculo " +
            "WHERE rs.data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "GROUP BY v.id_veiculo, v.modelo ORDER BY total_km DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarTop5KmSemana();

    @Query(value = "SELECT COALESCE(SUM(km_rodados), 0.0) FROM registro_saida " +
            "WHERE id_veiculo = :idVeiculo AND data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
    Double totalKmSemana(@Param("idVeiculo") Integer idVeiculo);

    @Query(value = "SELECT COUNT(*) FROM registro_saida " +
            "WHERE id_veiculo = :idVeiculo AND data_hora_saida >= DATE_SUB(NOW(), INTERVAL 7 DAY)", nativeQuery = true)
    Long totalSaidasSemana(@Param("idVeiculo") Integer idVeiculo);

    // Relatório técnico
    @Query(value = "SELECT rs.matricula_usuario, u.nome, COUNT(rs.id_saida), COALESCE(SUM(rs.km_rodados), 0) " +
            "FROM registro_saida rs JOIN usuario u ON u.matricula = rs.matricula_usuario " +
            "WHERE rs.data_hora_saida >= :dataInicio GROUP BY rs.matricula_usuario, u.nome", nativeQuery = true)
    List<Object[]> buscarSaidasKmPorTecnico(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT rs.matricula_usuario, rs.km_rodados FROM registro_saida rs WHERE rs.data_hora_saida >= :dataInicio", nativeQuery = true)
    List<Object[]> buscarKmPorSemana(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT COUNT(*) FROM registro_saida WHERE matricula_usuario = :matricula AND data_hora_saida >= :dataInicio", nativeQuery = true)
    long countPorMatriculaEPeriodo(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT COALESCE(SUM(km_rodados), 0) FROM registro_saida WHERE matricula_usuario = :matricula AND data_hora_saida >= :dataInicio", nativeQuery = true)
    BigDecimal sumKmPorMatriculaEPeriodo(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT MAX(km_rodados) FROM registro_saida WHERE matricula_usuario = :matricula AND data_hora_saida >= :dataInicio", nativeQuery = true)
    BigDecimal buscarMaiorKm(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT MAX(TIMESTAMPDIFF(SECOND, data_hora_saida, data_retorno)) / 3600 FROM registro_saida WHERE matricula_usuario = :matricula AND data_hora_saida >= :dataInicio AND status = 'concluido'", nativeQuery = true)
    Double buscarMaiorDuracaoHoras(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT COALESCE(AVG(TIMESTAMPDIFF(SECOND, data_hora_saida, data_retorno)) / 3600, 0) FROM registro_saida WHERE matricula_usuario = :matricula AND data_hora_saida >= :dataInicio AND status = 'concluido'", nativeQuery = true)
    Double calcularTempoMedioHoras(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT local_destino, COUNT(*) as freq FROM registro_saida WHERE matricula_usuario = :matricula GROUP BY local_destino ORDER BY freq DESC LIMIT 5", nativeQuery = true)
    List<Object[]> buscarDestinosMaisFrequentes(@Param("matricula") Integer matricula);

    // CORRIGIDO: era "ts.nome" — coluna correta é "nome_servico"
    @Query(value = "SELECT ts.nome_servico, COUNT(rs.id_saida) FROM registro_saida rs " +
            "JOIN tipo_servico ts ON ts.id_tipo_servico = rs.id_tipo_servico " +
            "WHERE rs.matricula_usuario = :matricula AND rs.data_hora_saida >= :dataInicio " +
            "GROUP BY ts.nome_servico", nativeQuery = true)
    List<Object[]> buscarServicosDoTecnico(@Param("matricula") Integer matricula,
                                           @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT DISTINCT CONCAT(v.modelo, ' (', v.prefixo, ')') FROM registro_saida rs " +
            "JOIN veiculo v ON v.id_veiculo = rs.id_veiculo WHERE rs.matricula_usuario = :matricula", nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("matricula") Integer matricula);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE colaborador_ativo = true", nativeQuery = true)
    long countTecnicosAtivos();

    // NOVO: busca saídas para relatório de troca de óleo de veículo
    @Query(value = "SELECT rs.* FROM registro_saida rs " +
            "JOIN tipo_servico ts ON ts.id_tipo_servico = rs.id_tipo_servico " +
            "WHERE rs.id_veiculo = :idVeiculo AND ts.eh_troca_oleo = true " +
            "ORDER BY rs.data_hora_saida DESC", nativeQuery = true)
    List<DepartureLog> findByVeiculoIdVeiculoAndTipoServicoEhTrocaOleoTrue(@Param("idVeiculo") Integer idVeiculo);
}