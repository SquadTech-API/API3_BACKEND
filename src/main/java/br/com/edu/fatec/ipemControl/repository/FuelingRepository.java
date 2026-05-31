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

    // ── Busca por saída ───────────────────────────────────────────

    List<Fueling> findByDepartureLog(DepartureLog departureLog);

    List<Fueling> findByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(Integer vehicleId);

    List<Fueling> findAllByOrderByFuelingDatetimeDesc();

    List<Fueling> findByFuelingDatetimeBetweenOrderByFuelingDatetimeDesc(
            LocalDateTime start, LocalDateTime end);

    List<Fueling> findByDepartureLogVehicleLicensePlateOrderByFuelingDatetimeDesc(
            String licensePlate);

    List<Fueling> findByFuelingDatetimeAfterOrderByFuelingDatetimeDesc(
            LocalDateTime startDate);

    Optional<Fueling> findTopByDepartureLogVehicleIdOrderByFuelingDatetimeDesc(
            Integer vehicleId);

    // ── Busca por viatura (sem departure log) ─────────────────────

    @Query(value = """
        SELECT f.* FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE dl.vehicle_id = :vehicleId
        """, nativeQuery = true)
    List<Fueling> findByVehicleId(@Param("vehicleId") Integer vehicleId);

    // ── Dashboard de viaturas ─────────────────────────────────────

    @Query(value = """
        SELECT COALESCE(SUM(f.total_value), 0.0) FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE dl.vehicle_id = :vehicleId
          AND f.fueling_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)
    Double totalWeeklySpending(@Param("vehicleId") Integer vehicleId);

    @Query(value = """
        SELECT COALESCE(SUM(f.liters), 0.0) FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE dl.vehicle_id = :vehicleId
          AND f.fueling_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)
    Double totalWeeklyLiters(@Param("vehicleId") Integer vehicleId);

    // ── Relatório de técnicos — TechnicianReportService ──────────

    @Query(value = """
        SELECT dl.user_registration, COALESCE(SUM(f.total_value), 0)
        FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE f.fueling_datetime >= :startDate
        GROUP BY dl.user_registration
        """, nativeQuery = true)
    List<Object[]> findCostByTechnician(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT COALESCE(SUM(f.total_value), 0) FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE dl.user_registration = :registration
          AND f.fueling_datetime >= :startDate
        """, nativeQuery = true)
    BigDecimal sumSpendingByRegistrationAndPeriod(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT COUNT(*) FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        WHERE dl.user_registration = :registration
          AND f.fueling_datetime >= :startDate
        """, nativeQuery = true)
    long countByRegistrationAndPeriod(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // ── Relatório de abastecimentos — FuelingService ──────────────

    @Query(value = """
        SELECT YEARWEEK(f.fueling_datetime) AS week,
               COALESCE(SUM(f.total_value), 0),
               COALESCE(SUM(f.liters), 0)
        FROM fueling f
        WHERE f.fueling_datetime >= :startDate
        GROUP BY week
        ORDER BY week DESC
        LIMIT 4
        """, nativeQuery = true)
    List<Object[]> findWeeklyStatistics(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT v.license_plate,
               COALESCE(SUM(f.liters), 0),
               COALESCE(SUM(dl.driven_mileage), 0),
               CASE WHEN SUM(f.liters) > 0
                    THEN SUM(dl.driven_mileage) / SUM(f.liters)
                    ELSE 0 END,
               COALESCE(SUM(f.total_value), 0)
        FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        JOIN vehicle v ON v.id = dl.vehicle_id
        WHERE f.fueling_datetime >= :startDate
        GROUP BY v.license_plate
        """, nativeQuery = true)
    List<Object[]> findVehicleConsumption(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT dl.user_registration, u.full_name,
               COUNT(f.id), COALESCE(SUM(f.total_value), 0)
        FROM fueling f
        JOIN departure_log dl ON dl.id = f.departure_log_id
        JOIN users u ON u.registration = dl.user_registration
        WHERE f.fueling_datetime >= :startDate
        GROUP BY dl.user_registration, u.full_name
        ORDER BY SUM(f.total_value) DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> findUserRankings(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT f.station_name, f.station_city, COUNT(f.id)
        FROM fueling f
        WHERE f.fueling_datetime >= :startDate
          AND f.station_name IS NOT NULL
        GROUP BY f.station_name, f.station_city
        ORDER BY COUNT(f.id) DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> findStationRankings(@Param("startDate") LocalDateTime startDate);

    @Query(value = """
        SELECT ft.name, COUNT(f.id)
        FROM fueling f
        JOIN fuel_type ft ON ft.id = f.fuel_type_id
        WHERE f.fueling_datetime >= :startDate
        GROUP BY ft.name
        """, nativeQuery = true)
    List<Object[]> findFuelDistribution(@Param("startDate") LocalDateTime startDate);
}