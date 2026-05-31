package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartureLogRepository extends JpaRepository<DepartureLog, Integer> {

    // ── Saída ativa ───────────────────────────────────────────────

    Optional<DepartureLog> findTopByUserRegistrationAndStatusOrderByDepartureDatetimeDesc(
            Integer userRegistration, String status);

    Optional<DepartureLog> findTopByVehicleIdAndStatusOrderByDepartureDatetimeDesc(
            Integer vehicleId, String status);

    Optional<DepartureLog> findTopByUserRegistrationOrderByDepartureDatetimeDesc(
            Integer registration);

    // ── Filtros básicos ───────────────────────────────────────────

    List<DepartureLog> findByStatus(String status);

    List<DepartureLog> findByVehicleIdOrderByDepartureDatetimeDesc(Integer vehicleId);

    List<DepartureLog> findByVehicleIdAndDepartureDatetimeBetween(
            Integer vehicleId, LocalDateTime start, LocalDateTime end);

    List<DepartureLog> findByUserRegistrationAndDepartureDatetimeBetween(
            Integer registration, LocalDateTime start, LocalDateTime end);

    List<DepartureLog> findBySecondUserRegistrationAndDepartureDatetimeBetween(
            Integer registration, LocalDateTime start, LocalDateTime end);

    List<DepartureLog> findByVehicleIdAndServiceTypeIsOilChangeTrueOrderByDepartureDatetimeDesc(
            Integer vehicleId);

    // ── SGI (#A09) ────────────────────────────────────────────────

    List<DepartureLog> findBySgiTranscribedFalseAndStatusOrderByDepartureDatetimeDesc(
            String status);

    @Modifying
    @Transactional
    @Query("UPDATE DepartureLog d SET d.sgiTranscribed = true WHERE d.id = :id")
    void markAsSgiTranscribed(@Param("id") Integer id);

    // ── Verifica condutor ativo ───────────────────────────────────

    @Query("""
        SELECT COUNT(d) > 0 FROM DepartureLog d
        WHERE d.status = 'in_progress'
          AND (d.user.registration = :registration
               OR d.secondUser.registration = :registration)
        """)
    boolean existsActiveByUserRegistration(@Param("registration") Integer registration);

    // ══════════════════════════════════════════════════════════════
    // RELATÓRIO DE TÉCNICOS — TechnicianReportService
    // ══════════════════════════════════════════════════════════════

    // Saídas e KM por técnico em um período
    @Query(value = """
        SELECT dl.user_registration, u.full_name,
               COUNT(dl.id), COALESCE(SUM(dl.driven_mileage), 0)
        FROM departure_log dl
        JOIN users u ON u.registration = dl.user_registration
        WHERE dl.departure_datetime >= :startDate
        GROUP BY dl.user_registration, u.full_name
        """, nativeQuery = true)
    List<Object[]> buscarSaidasKmPorTecnico(@Param("startDate") LocalDateTime startDate);

    // KM por semana (para gráfico de linha)
    @Query(value = """
        SELECT dl.user_registration,
               COALESCE(SUM(dl.driven_mileage), 0)
        FROM departure_log dl
        WHERE dl.departure_datetime >= :startDate
        GROUP BY dl.user_registration
        """, nativeQuery = true)
    List<Object[]> buscarKmPorSemana(@Param("startDate") LocalDateTime startDate);

    // Contagem de saídas de um técnico em um período
    @Query(value = """
        SELECT COUNT(*) FROM departure_log
        WHERE user_registration = :registration
          AND departure_datetime >= :startDate
        """, nativeQuery = true)
    long countPorMatriculaEPeriodo(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // KM total de um técnico em um período
    @Query(value = """
        SELECT COALESCE(SUM(driven_mileage), 0) FROM departure_log
        WHERE user_registration = :registration
          AND departure_datetime >= :startDate
        """, nativeQuery = true)
    BigDecimal sumKmPorMatriculaEPeriodo(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Maior KM de uma saída do técnico
    @Query(value = """
        SELECT MAX(driven_mileage) FROM departure_log
        WHERE user_registration = :registration
          AND departure_datetime >= :startDate
        """, nativeQuery = true)
    BigDecimal buscarMaiorKm(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Maior duração de saída do técnico (em horas)
    @Query(value = """
        SELECT MAX(TIMESTAMPDIFF(SECOND, departure_datetime, return_datetime)) / 3600
        FROM departure_log
        WHERE user_registration = :registration
          AND departure_datetime >= :startDate
          AND status = 'completed'
        """, nativeQuery = true)
    Double buscarMaiorDuracaoHoras(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Tempo médio de saída do técnico (em horas)
    @Query(value = """
        SELECT COALESCE(
            AVG(TIMESTAMPDIFF(SECOND, departure_datetime, return_datetime)) / 3600, 0)
        FROM departure_log
        WHERE user_registration = :registration
          AND departure_datetime >= :startDate
          AND status = 'completed'
        """, nativeQuery = true)
    Double calcularTempoMedioHoras(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Top 5 destinos mais frequentes do técnico
    @Query(value = """
        SELECT destination, COUNT(*) as freq
        FROM departure_log
        WHERE user_registration = :registration
        GROUP BY destination
        ORDER BY freq DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> buscarDestinosMaisFrequentes(@Param("registration") Integer registration);

    // Serviços por tipo realizados pelo técnico
    @Query(value = """
        SELECT st.service_name, COUNT(dl.id)
        FROM departure_log dl
        JOIN service_type st ON st.id = dl.service_type_id
        WHERE dl.user_registration = :registration
          AND dl.departure_datetime >= :startDate
        GROUP BY st.service_name
        """, nativeQuery = true)
    List<Object[]> buscarServicosDoTecnico(
            @Param("registration") Integer registration,
            @Param("startDate") LocalDateTime startDate);

    // Viaturas utilizadas pelo técnico
    @Query(value = """
        SELECT DISTINCT CONCAT(v.model, ' (', v.prefix, ')')
        FROM departure_log dl
        JOIN vehicle v ON v.id = dl.vehicle_id
        WHERE dl.user_registration = :registration
        """, nativeQuery = true)
    List<String> buscarVeiculosUtilizados(@Param("registration") Integer registration);

    // Total de técnicos ativos no sistema
    @Query(value = """
        SELECT COUNT(*) FROM users
        WHERE active_employee = true
          AND user_type = 'technician'
        """, nativeQuery = true)
    long countActiveTechnicians();

    // ══════════════════════════════════════════════════════════════
    // DASHBOARD DE VIATURAS — VehicleDashboardService
    // ══════════════════════════════════════════════════════════════

    // Top 5 viaturas com mais KM na semana
    @Query(value = """
        SELECT v.id, v.model, COALESCE(SUM(dl.driven_mileage), 0) AS total_km
        FROM departure_log dl
        JOIN vehicle v ON v.id = dl.vehicle_id
        WHERE dl.departure_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        GROUP BY v.id, v.model
        ORDER BY total_km DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5WeeklyKilometers();

    // KM semanal de uma viatura específica
    @Query(value = """
        SELECT COALESCE(SUM(driven_mileage), 0.0) FROM departure_log
        WHERE vehicle_id = :vehicleId
          AND departure_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)
    Double totalWeeklyKilometers(@Param("vehicleId") Integer vehicleId);

    // Total de saídas semanais de uma viatura
    @Query(value = """
        SELECT COUNT(*) FROM departure_log
        WHERE vehicle_id = :vehicleId
          AND departure_datetime >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        """, nativeQuery = true)

    // ── Contagens — AdminDashboardService ────────────────────────
    long countByStatus(String status);
    long countBySgiTranscribedFalseAndStatus(String status);
}