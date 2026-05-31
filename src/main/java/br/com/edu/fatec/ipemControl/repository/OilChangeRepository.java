package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, Integer> {

    // ── Buscas por viatura ────────────────────────────────────────
    List<OilChange> findByVehicleIdOrderByCreatedAtDesc(Integer vehicleId);

    @Query("SELECT o FROM OilChange o WHERE o.vehicle.id = :vehicleId ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OilChange> findLatestByVehicle(@Param("vehicleId") Integer vehicleId);

    // ── Buscas por técnico (via departure_log) ────────────────────
    @Query("SELECT COUNT(o) FROM OilChange o WHERE o.departureLog.user.registration = :registration")
    Long countByDepartureLogUserRegistration(@Param("registration") Integer registration);

    @Query("""
        SELECT o FROM OilChange o
        WHERE o.departureLog.user.registration = :registration
        ORDER BY o.createdAt DESC
        LIMIT 1
        """)
    Optional<OilChange> findTopByDepartureLogUserRegistrationOrderByCreatedAtDesc(
            @Param("registration") Integer registration);

    // ── Relatório de abastecimentos (FuelingService) ──────────────
    List<OilChange> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);
}