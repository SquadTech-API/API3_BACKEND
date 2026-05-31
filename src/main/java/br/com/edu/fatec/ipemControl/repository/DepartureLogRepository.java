package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.DepartureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartureLogRepository extends JpaRepository<DepartureLog, Integer> {

    // Saída ativa do condutor principal
    Optional<DepartureLog> findTopByUserRegistrationAndStatusOrderByDepartureDatetimeDesc(
            Integer userRegistration, String status);

    // Saída ativa da viatura
    Optional<DepartureLog> findTopByVehicleIdAndStatusOrderByDepartureDatetimeDesc(
            Integer vehicleId, String status);

    // Saídas de uma viatura em um período
    List<DepartureLog> findByVehicleIdAndDepartureDatetimeBetween(
            Integer vehicleId, LocalDateTime start, LocalDateTime end);

    // Saídas de um técnico em um período
    List<DepartureLog> findByUserRegistrationAndDepartureDatetimeBetween(
            Integer registration, LocalDateTime start, LocalDateTime end);

    // Saídas onde o usuário é 2º condutor em um período
    List<DepartureLog> findBySecondUserRegistrationAndDepartureDatetimeBetween(
            Integer registration, LocalDateTime start, LocalDateTime end);

    // Saídas por status
    List<DepartureLog> findByStatus(String status);

    // Saídas de uma viatura ordenadas por data
    List<DepartureLog> findByVehicleIdOrderByDepartureDatetimeDesc(Integer vehicleId);

    // Saídas de troca de óleo de uma viatura
    List<DepartureLog> findByVehicleIdAndServiceTypeIsOilChangeTrueOrderByDepartureDatetimeDesc(
            Integer vehicleId);

    // Saídas não transcritas ao SGI (#A09)
    List<DepartureLog> findBySgiTranscribedFalseAndStatusOrderByDepartureDatetimeDesc(
            String status);

    // Marca como transcrito ao SGI (#A09)
    @Modifying
    @Transactional
    @Query("UPDATE DepartureLog d SET d.sgiTranscribed = true WHERE d.id = :id")
    void markAsSgiTranscribed(@Param("id") Integer id);

    // Verifica se o usuário é condutor principal ou 2º condutor em alguma saída ativa
    @Query("""
        SELECT COUNT(d) > 0 FROM DepartureLog d
        WHERE d.status = 'in_progress'
          AND (d.user.registration = :registration
               OR d.secondUser.registration = :registration)
        """)
    boolean existsActiveByUserRegistration(@Param("registration") Integer registration);
}