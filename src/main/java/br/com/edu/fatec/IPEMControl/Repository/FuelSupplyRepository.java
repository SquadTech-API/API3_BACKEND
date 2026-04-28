package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.FuelSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuelSupplyRepository extends JpaRepository<FuelSupply, Integer> {

    //Usado pelo VehicleService
    Optional<FuelSupply> findTopByExitRecordVehicleIdOrderByDateTimeDesc(Integer vehicleId);

    //Histórico por veículo
    List<FuelSupply> findByExitRecordVehicleIdOrderByDateTimeDesc(Integer vehicleId);

    //Todos os abastecimentos
    List<FuelSupply> findAllByOrderByDateTimeDesc();

    //Filtro por intervalo de datas
    List<FuelSupply> findByDateTimeBetweenOrderByDateTimeDesc(
            LocalDateTime from, LocalDateTime to);

    //Filtro por placa
    List<FuelSupply> findByExitRecordVehiclePlateOrderByDateTimeDesc(String plate);

    //Filtro por período
    List<FuelSupply> findByDateTimeAfterOrderByDateTimeDesc(LocalDateTime from);

    //Gasto e litros por semana
    @Query("SELECT WEEK(f.dateTime), SUM(f.totalValue), SUM(f.litersAmount) " +
            "FROM FuelSupply f WHERE f.dateTime >= :startDate " +
            "GROUP BY WEEK(f.dateTime) ORDER BY WEEK(f.dateTime)")
    List<Object[]> findWeeklyStats(@Param("startDate") LocalDateTime startDate);

    //Ranking de postos
    @Query("SELECT f.stationName, f.stationCity, COUNT(f) " +
            "FROM FuelSupply f WHERE f.dateTime >= :startDate " +
            "GROUP BY f.stationName, f.stationCity " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> findTopStations(@Param("startDate") LocalDateTime startDate);

    //Distribuição por tipo de combustível
    @Query("SELECT f.fuelType, COUNT(f) " +
            "FROM FuelSupply f WHERE f.dateTime >= :startDate " +
            "GROUP BY f.fuelType")
    List<Object[]> findFuelTypeDistribution(@Param("startDate") LocalDateTime startDate);

    //Consumo médio por veículo
    @Query("SELECT v.plate, SUM(f.litersAmount), SUM(rs.kmRodados), " +
            "CASE WHEN SUM(f.litersAmount) > 0 " +
            "THEN SUM(rs.kmRodados) / SUM(f.litersAmount) ELSE 0 END, " +
            "SUM(f.totalValue) " +
            "FROM FuelSupply f " +
            "JOIN f.exitRecord rs " +
            "JOIN rs.veiculo v " +
            "WHERE f.dateTime >= :startDate " +
            "GROUP BY v.plate")
    List<Object[]> findConsumptionByVehicle(@Param("startDate") LocalDateTime startDate);

    //Ranking de usuários
    @Query("SELECT u.nome, COUNT(f), SUM(f.totalValue) " +
            "FROM FuelSupply f " +
            "JOIN f.exitRecord rs " +
            "JOIN rs.usuario u " +
            "WHERE f.dateTime >= :startDate " +
            "GROUP BY u.nome " +
            "ORDER BY COUNT(f) DESC")
    List<Object[]> findUserRanking(@Param("startDate") LocalDateTime startDate);
}