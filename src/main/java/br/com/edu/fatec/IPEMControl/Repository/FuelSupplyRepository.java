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
public interface FuelSupplyRepository extends JpaRepository<Abastecimento, Integer> {

    // Usado pelo VehicleService
    Optional<Abastecimento> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer vehicleId);

    // Histórico por veículo
    List<Abastecimento> findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer vehicleId);

    // Todos os abastecimentos
    List<Abastecimento> findAllByOrderByDataHoraDesc();

    // Filtro por intervalo de datas
    List<Abastecimento> findByDataHoraBetweenOrderByDataHoraDesc(
            LocalDateTime from, LocalDateTime to);

    // Filtro por placa
    List<Abastecimento> findByRegistroSaidaVeiculoPlacaOrderByDataHoraDesc(String plate);

    // Filtro por período
    List<Abastecimento> findByDataHoraAfterOrderByDataHoraDesc(LocalDateTime from);

    //Gasto e litros por semana
    @Query(value = """
        SELECT WEEK(data_hora) as week,
               SUM(valor_total) as spent,
               SUM(quantidade_litros) as liters
        FROM abastecimento
        WHERE data_hora >= :startDate
        GROUP BY WEEK(data_hora)
        ORDER BY week
        """, nativeQuery = true)
    List<Object[]> findWeeklyStats(@Param("startDate") LocalDateTime startDate);

    //Ranking de postos
    @Query(value = """
        SELECT posto_nome, posto_cidade, COUNT(*) as visit_count
        FROM abastecimento
        WHERE data_hora >= :startDate
        GROUP BY posto_nome, posto_cidade
        ORDER BY visit_count DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> findTopStations(@Param("startDate") LocalDateTime startDate);

    //Distribuição por tipo de combustível
    @Query(value = """
        SELECT tipo_combustivel,
               ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 1) as percentage
        FROM abastecimento
        WHERE data_hora >= :startDate
        GROUP BY tipo_combustivel
        """, nativeQuery = true)
    List<Object[]> findFuelTypeDistribution(@Param("startDate") LocalDateTime startDate);

    //Consumo médio por veículo
    @Query(value = """
        SELECT v.placa,
               SUM(a.quantidade_litros) as total_liters,
               SUM(rs.km_rodados) as total_km,
               CASE WHEN SUM(a.quantidade_litros) > 0
                    THEN SUM(rs.km_rodados) / SUM(a.quantidade_litros)
                    ELSE 0 END as consumption_kml,
               SUM(a.valor_total) as total_spent
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        JOIN veiculo v ON rs.id_veiculo = v.id_veiculo
        WHERE a.data_hora >= :startDate
        GROUP BY v.placa
        """, nativeQuery = true)
    List<Object[]> findConsumptionByVehicle(@Param("startDate") LocalDateTime startDate);

    //Ranking de usuários
    @Query(value = """
        SELECT u.nome, COUNT(a.id_abastecimento) as fuel_supply_count, SUM(a.valor_total) as total_spent
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        JOIN usuario u ON rs.matricula_usuario = u.matricula
        WHERE a.data_hora >= :startDate
        GROUP BY u.nome
        ORDER BY fuel_supply_count DESC
        """, nativeQuery = true)
    List<Object[]> findUserRanking(@Param("startDate") LocalDateTime startDate);
}