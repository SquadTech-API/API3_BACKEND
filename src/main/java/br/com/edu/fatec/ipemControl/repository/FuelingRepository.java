package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import br.com.edu.fatec.ipemControl.entity.Fueling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuelingRepository extends JpaRepository<Fueling, Integer> {

    // Abastecimentos de uma viatura
    @Query("SELECT f FROM Fueling f WHERE f.departureLog.vehicle.id = :vehicleId")
    List<Fueling> findByVehicleId(@Param("vehicleId") Integer vehicleId);

    // Abastecimentos de uma saída
    List<Fueling> findByDepartureLog(DepartureLog departureLog);

    // Último abastecimento de uma viatura
    Optional<Fueling> findTopByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(Integer vehicleId);

    // Abastecimentos de uma viatura ordenados por data
    List<Fueling> findByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(Integer vehicleId);

    // Todos os abastecimentos ordenados por data
    List<Fueling> findAllByOrderByFuelingDatetimeDesc();

    // Abastecimentos em um período
    List<Fueling> findByFuelingDatetimeBetweenOrderByFuelingDatetimeDesc(
            LocalDateTime start, LocalDateTime end);

    // Abastecimentos após uma data
    List<Fueling> findByFuelingDatetimeAfterOrderByFuelingDatetimeDesc(LocalDateTime start);

    // Abastecimentos por placa da viatura
    List<Fueling> findByDepartureLogVehicleLicensePlateOrderByFuelingDatetimeDesc(
            String licensePlate);

    // Último abastecimento de um técnico
    Optional<Fueling> findTopByDepartureLogUserRegistrationOrderByFuelingDatetimeDesc(
            Integer registration);

    // Custo total de abastecimento de um técnico em um período
    @Query(value = """
        SELECT COALESCE(SUM(f.total_value), 0)
        FROM fueling f
        JOIN departure_log dl ON f.departure_log_id = dl.id
        WHERE dl.user_registration = :registration
          AND dl.departure_datetime >= :startDate
        """, nativeQuery = true)
    BigDecimal sumSpendingByRegistrationAndPeriod(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Total de abastecimentos de um técnico em um período
    @Query(value = """
        SELECT COUNT(f.id)
        FROM fueling f
        JOIN departure_log dl ON f.departure_log_id = dl.id
        WHERE dl.user_registration = :registration
          AND dl.departure_datetime >= :startDate
        """, nativeQuery = true)
    long countByRegistrationAndPeriod(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Gasto semanal de uma viatura
    @Query(value = """
        SELECT COALESCE(SUM(f.total_value), 0)
        FROM fueling f
        JOIN departure_log dl ON f.departure_log_id = dl.id
        WHERE dl.vehicle_id = :vehicleId
          AND dl.departure_datetime >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalWeeklySpending(@Param("vehicleId") Integer vehicleId);

    // Litros abastecidos semanalmente por viatura
    @Query(value = """
        SELECT COALESCE(SUM(f.liters), 0)
        FROM fueling f
        JOIN departure_log dl ON f.departure_log_id = dl.id
        WHERE dl.vehicle_id = :vehicleId
          AND dl.departure_datetime >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalWeeklyLiters(@Param("vehicleId") Integer vehicleId);

    // Estatísticas semanais (relatório)
    @Query("SELECT WEEK(f.fuelingDatetime), SUM(f.totalValue), SUM(f.liters) " +
            "FROM Fueling f WHERE f.fuelingDatetime >= :startDate " +
            "GROUP BY WEEK(f.fuelingDatetime) ORDER BY WEEK(f.fuelingDatetime)")
    List<Object[]> findWeeklyStatistics(@Param("startDate") LocalDateTime startDate);

    // Ranking de postos mais utilizados
    @Query("SELECT f.stationName, f.stationCity, COUNT(f) " +
            "FROM Fueling f WHERE f.fuelingDatetime >= :startDate " +
            "GROUP BY f.stationName, f.stationCity ORDER BY COUNT(f) DESC")
    List<Object[]> findStationRankings(@Param("startDate") LocalDateTime startDate);

    // Distribuição por tipo de combustível
    @Query("SELECT f.fuelType.name, COUNT(f) " +
            "FROM Fueling f WHERE f.fuelingDatetime >= :startDate " +
            "GROUP BY f.fuelType.name ORDER BY COUNT(f) DESC")
    List<Object[]> findFuelDistribution(@Param("startDate") LocalDateTime startDate);

    // Consumo por viatura (km/L, custo)
    @Query("SELECT v.licensePlate, SUM(f.liters), SUM(dl.drivenMileage), " +
            "CASE WHEN SUM(f.liters) > 0 THEN SUM(dl.drivenMileage) / SUM(f.liters) ELSE 0 END, " +
            "SUM(f.totalValue) " +
            "FROM Fueling f " +
            "JOIN f.departureLog dl " +
            "JOIN dl.vehicle v " +
            "WHERE f.fuelingDatetime >= :startDate " +
            "GROUP BY v.licensePlate")
    List<Object[]> findVehicleConsumption(@Param("startDate") LocalDateTime startDate);

    // Ranking de usuários que mais abastecem
    @Query("SELECT u.fullName, COUNT(f), SUM(f.totalValue) " +
            "FROM Fueling f " +
            "JOIN f.departureLog dl " +
            "JOIN dl.user u " +
            "WHERE f.fuelingDatetime >= :startDate " +
            "GROUP BY u.fullName ORDER BY COUNT(f) DESC")
    List<Object[]> findUserRankings(@Param("startDate") LocalDateTime startDate);
}