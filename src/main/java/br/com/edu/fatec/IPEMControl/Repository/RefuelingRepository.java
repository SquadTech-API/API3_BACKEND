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
public interface RefuelingRepository extends JpaRepository<Fueling, Integer> {

    @Query("SELECT fueling FROM Fueling fueling JOIN fueling.departureLog departureLog WHERE departureLog.vehicle.vehicleId = :vehicleId")
    List<Fueling> findByVehicleVehicleId(@Param("vehicleId") Integer vehicleId);

    List<Fueling> findByDepartureLog(DepartureLog departureLog);

    Optional<Fueling> findTopByDepartureLogVehicleVehicleIdOrderByDateTimeDesc(Integer vehicleId);

    List<Fueling> findByDepartureLogVehicleVehicleIdOrderByDateTimeDesc(Integer vehicleId);

    List<Fueling> findAllByOrderByDateTimeDesc();

    @Query(value = """
        SELECT rs.matricula_usuario,
               COALESCE(SUM(a.valor_total), 0),
               COUNT(a.id_abastecimento)
        FROM registro_saida rs
        LEFT JOIN abastecimento a ON a.id_saida = rs.id_saida
        WHERE rs.data_hora_saida >= :startDate
        GROUP BY rs.matricula_usuario
        """, nativeQuery = true)
    List<Object[]> findCostByTechnician(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :registration
          AND rs.data_hora_saida >= :startDate
        """, nativeQuery = true)
    BigDecimal sumSpendingByRegistrationAndPeriod(@Param("registration") Integer registration,
                                                  @Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT COUNT(a.id_abastecimento)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :registration
          AND rs.data_hora_saida >= :startDate
        """, nativeQuery = true)
    long countFuelingsByRegistrationAndPeriod(@Param("registration") Integer registration,
                                              @Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT a.*
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.matricula_usuario = :registration
        ORDER BY a.data_hora DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<Fueling> findLatestFuelingByTechnician(@Param("registration") Integer registration);

    // ── Queries para dashboard de veículos ───────────────────────────────────

    @Query(value = """
        SELECT COALESCE(SUM(a.valor_total), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :vehicleId
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalWeeklySpending(@Param("vehicleId") Integer vehicleId);

    @Query(value = """
        SELECT COALESCE(SUM(a.quantidade_litros), 0)
        FROM abastecimento a
        JOIN registro_saida rs ON a.id_saida = rs.id_saida
        WHERE rs.id_veiculo = :vehicleId
          AND rs.data_hora_saida >= NOW() - INTERVAL 7 DAY
        """, nativeQuery = true)
    Double totalWeeklyLiters(@Param("vehicleId") Integer vehicleId);

    // Filtros e Estatísticas
    List<Fueling> findByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);

    List<Fueling> findByDepartureLogVehicleLicensePlateOrderByDateTimeDesc(String licensePlate);

    List<Fueling> findByDateTimeAfterOrderByDateTimeDesc(LocalDateTime start);

    @Query("SELECT WEEK(a.dateTime), SUM(a.totalValue), SUM(a.litersAmount) " +
            "FROM Fueling a WHERE a.dateTime >= :startDate " +
            "GROUP BY WEEK(a.dateTime) ORDER BY WEEK(a.dateTime)")
    List<Object[]> findWeeklyStatistics(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.gasStationName, a.gasStationCity, COUNT(a) " +
            "FROM Fueling a WHERE a.dateTime >= :startDate " +
            "GROUP BY a.gasStationName, a.gasStationCity " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findStationRankings(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT a.fuelType, COUNT(a) " +
            "FROM Fueling a WHERE a.dateTime >= :startDate " +
            "GROUP BY a.fuelType " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findFuelDistribution(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT vehicle.licensePlate, SUM(fueling.litersAmount), SUM(departureLog.drivenKm), " +
            "CASE WHEN SUM(fueling.litersAmount) > 0 " +
            "THEN SUM(departureLog.drivenKm) / SUM(fueling.litersAmount) ELSE 0 END, " +
            "SUM(fueling.totalValue) " +
            "FROM Fueling fueling " +
            "JOIN fueling.departureLog departureLog " +
            "JOIN departureLog.vehicle vehicle " +
            "WHERE fueling.dateTime >= :startDate " +
            "GROUP BY vehicle.licensePlate")
    List<Object[]> findVehicleConsumption(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT appUser.name, COUNT(fueling), SUM(fueling.totalValue) " +
            "FROM Fueling fueling " +
            "JOIN fueling.departureLog departureLog " +
            "JOIN departureLog.user appUser " +
            "WHERE fueling.dateTime >= :startDate " +
            "GROUP BY appUser.name " +
            "ORDER BY COUNT(fueling) DESC")
    List<Object[]> findUserRankings(@Param("startDate") LocalDateTime startDate);
}
