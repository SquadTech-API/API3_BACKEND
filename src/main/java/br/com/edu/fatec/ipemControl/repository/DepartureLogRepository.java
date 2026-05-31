package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.DepartureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartureLogRepository extends JpaRepository<DepartureLog, Integer> {

    Optional<DepartureLog> findTopByUserRegistrationAndStatusOrderByDateTimeDepartureDesc(Integer userRegistration, String status);

    Optional<DepartureLog> findTopByVehicleVehicleIdAndStatusOrderByDateTimeDepartureDesc(Integer vehicleId, String status);

    List<DepartureLog> findByVehicleVehicleIdAndServiceTypeOilChangeSTTrue(Integer vehicleId);

    List<DepartureLog> findByVehicleVehicleIdAndDateTimeDepartureBetween(Integer vehicleId, LocalDateTime start, LocalDateTime end);
}