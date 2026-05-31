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

    // Total de trocas de óleo feitas pelo usuário
    long countByDepartureLogUserRegistration(Integer registration);

    // Última troca feita pelo usuário
    Optional<OilChange> findTopByDepartureLogUserRegistrationOrderByCreatedAtDesc(
            Integer registration);

    // Última troca de uma viatura
    @Query("SELECT o FROM OilChange o WHERE o.vehicle.id = :vehicleId ORDER BY o.createdAt DESC")
    Optional<OilChange> findLatestByVehicle(@Param("vehicleId") Integer vehicleId);

    // Histórico de trocas de uma viatura ordenado por data
    List<OilChange> findByVehicleIdOrderByCreatedAtDesc(Integer vehicleId);

    // Trocas registradas após uma data
    List<OilChange> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);

    // Última troca vinculada a uma saída
    Optional<OilChange> findTopByDepartureLogIdOrderByCreatedAtDesc(Integer departureLogId);
}