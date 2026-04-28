package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.OilMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OilMaintenanceRepository extends JpaRepository<OilMaintenance, Integer> {

    //Manutenção de óleo mais recente para um veículo específico
    @Query("SELECT o FROM OilMaintenance o " +
            "JOIN o.exitRecord rs " +
            "JOIN rs.vehicle v " +
            "WHERE v.id = :vehicleId " +
            "ORDER BY o.createdAt DESC")
    Optional<OilMaintenance> findLatestByVehicleId(@Param("vehicleId") Integer vehicleId);

    //Manutenção do óleo após uma determinada data
    List<OilMaintenance> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime from);
}