package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, Integer> {

    long countByDepartureLogUserRegistration(Integer registration);

    Optional<OilChange> findTopByDepartureLogUserRegistrationOrderByCreatedAtDesc(Integer registration);

    @Query("SELECT oilChange FROM OilChange oilChange WHERE oilChange.vehicle.vehicleId = :vehicleId ORDER BY oilChange.createdAt DESC")
    Optional<OilChange> findLatestByVehicle(@Param("vehicleId") Integer vehicleId);

    List<OilChange> findByVehicleVehicleIdOrderByCreatedAtDesc(Integer vehicleId);
    
    List<OilChange> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);

    Optional<OilChange> findTopByDepartureLogDepartureLogIdOrderByCreatedAtDesc(Integer exitId);
}
