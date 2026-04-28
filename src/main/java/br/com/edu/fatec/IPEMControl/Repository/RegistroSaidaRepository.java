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

    // 1. Relatórios de Dashboard e Uso Mensal (CORRIGIDO PARA O RELATORIOSERVICE LINHA 40)
    List<RegistroSaida> findByDataRetornoBetween(LocalDateTime inicio, LocalDateTime fim);

    // Este método precisa seguir exatamente a ordem: Matricula, DataInicio, DataFim
    List<RegistroSaida> findByUsuarioMatriculaAndDataHoraSaidaBetween(Integer matricula, LocalDateTime inicio, LocalDateTime fim);

    // 2. Controle de Fluxo e Status
    Optional<RegistroSaida> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer idVeiculo);
    Optional<RegistroSaida> findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(Integer matricula, String status);
    Optional<RegistroSaida> findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(Integer idVeiculo, String status);
    Optional<RegistroSaida> findTopByUsuarioMatriculaOrderByDataHoraSaidaDesc(Integer matricula);

    // 3. Dashboard de Veículos
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

    // 4. Relatório Técnico - Listas e Estatísticas
    @Query(value = "SELECT rs.matricula_usuario, u.nome, COUNT(rs.id_saida), COALESCE(SUM(rs.km_rodados), 0) " +
            "FROM registro_saida rs JOIN usuario u ON u.matricula = rs.matricula_usuario " +
            "WHERE rs.data_hora_saida >= :dataInicio GROUP BY rs.matricula_usuario, u.nome", nativeQuery = true)
    List<Object[]> buscarSaidasKmPorTecnico(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT rs.matricula_usuario, rs.km_rodados FROM registro_saida rs WHERE rs.data_hora_saida >= :dataInicio", nativeQuery = true)
    List<Object[]> buscarKmPorSemana(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT rs.matricula_usuario, rs.id_saida FROM registro_saida rs WHERE rs.data_hora_saida >= :dataInicio", nativeQuery = true)
    List<Object[]> buscarQuantidadeSaidasPorSemana(@Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT rs.matricula_usuario, a.valor_total FROM registro_saida rs JOIN abastecimento a ON a.id_saida = rs.id_saida WHERE rs.data_hora_saida >= :dataInicio", nativeQuery = true)
    List<Object[]> buscarCustoTotalPorSemana(@Param("dataInicio") LocalDateTime dataInicio);

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

    @Query(value = "SELECT ts.nome, COUNT(rs.id_saida) FROM registro_saida rs JOIN tipo_servico ts ON ts.id_tipo_servico = rs.id_tipo_servico WHERE rs.matricula_usuario = :matricula AND rs.data_hora_saida >= :dataInicio GROUP BY ts.nome", nativeQuery = true)
    List<Object[]> buscarServicosDoTecnico(@Param("matricula") Integer matricula, @Param("dataInicio") LocalDateTime dataInicio);

    @Query(value = "SELECT DISTINCT CONCAT(v.modelo, ' (', v.prefixo, ')') FROM registro_saida rs JOIN veiculo v ON v.id_veiculo = rs.id_veiculo WHERE rs.matricula_usuario = :matricula", nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("matricula") Integer matricula);

    // 5. Métodos Gerais
    @Query(value = "SELECT COUNT(*) FROM usuario WHERE colaborador_ativo = true", nativeQuery = true)
    long countTecnicosAtivos();

    List<RegistroSaida> findByStatus(String status);
}