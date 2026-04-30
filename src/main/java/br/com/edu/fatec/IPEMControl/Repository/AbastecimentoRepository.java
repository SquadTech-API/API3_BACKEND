package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Integer> {

    // Usado pelo VeiculoService
    Optional<Abastecimento> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    // Histórico por veículo
    List<Abastecimento> findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);

    // Todos os abastecimentos
    List<Abastecimento> findAllByOrderByDataHoraDesc();

    // Filtro por intervalo de datas
    List<Abastecimento> findByDataHoraBetweenOrderByDataHoraDesc(
            LocalDateTime inicio, LocalDateTime fim);

    // Filtro por placa
    List<Abastecimento> findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(String placa);

    // Filtro por período
    List<Abastecimento> findByDataHoraAfterOrderByDataHoraDesc(LocalDateTime inicio);

    // Gasto e litros por semana
    @Query("SELECT WEEK(a.dataHora), SUM(a.valorTotal), SUM(a.quantidadeLitros) " +
            "FROM Abastecimento a WHERE a.dataHora >= :dataInicio " +
            "GROUP BY WEEK(a.dataHora) ORDER BY WEEK(a.dataHora)")
    List<Object[]> buscarEstatisticasSemanas(@Param("dataInicio") LocalDateTime dataInicio);

    // Ranking de postos
    @Query("SELECT a.postoNome, a.postoCidade, COUNT(a) " +
            "FROM Abastecimento a WHERE a.dataHora >= :dataInicio " +
            "GROUP BY a.postoNome, a.postoCidade " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarRankingPostos(@Param("dataInicio") LocalDateTime dataInicio);

    // Distribuição por tipo de combustível
    @Query("SELECT a.tipoCombustivel, COUNT(a) " +
            "FROM Abastecimento a WHERE a.dataHora >= :dataInicio " +
            "GROUP BY a.tipoCombustivel " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarDistribuicaoCombustivel(@Param("dataInicio") LocalDateTime dataInicio);

    // Consumo médio por veículo
    @Query("SELECT v.placa, SUM(a.quantidadeLitros), SUM(rs.kmRodados), " +
            "CASE WHEN SUM(a.quantidadeLitros) > 0 " +
            "THEN SUM(rs.kmRodados) / SUM(a.quantidadeLitros) ELSE 0 END, " +
            "SUM(a.valorTotal) " +
            "FROM Abastecimento a " +
            "JOIN a.registroSaida rs " +
            "JOIN rs.veiculo v " +
            "WHERE a.dataHora >= :dataInicio " +
            "GROUP BY v.placa")
    List<Object[]> buscarConsumoPorVeiculo(@Param("dataInicio") LocalDateTime dataInicio);

    // Ranking de usuários
    @Query("SELECT u.nome, COUNT(a), SUM(a.valorTotal) " +
            "FROM Abastecimento a " +
            "JOIN a.registroSaida rs " +
            "JOIN rs.usuario u " +
            "WHERE a.dataHora >= :dataInicio " +
            "GROUP BY u.nome " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> buscarRankingUsuarios(@Param("dataInicio") LocalDateTime dataInicio);
}