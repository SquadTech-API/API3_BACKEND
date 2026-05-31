package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.VehicleService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Repository para ServiceVehicle.
 * Antes não existia — o VehicleServiceSyncService não tinha como persistir vínculos.
 */
@Repository
public interface ServiceVehicleRepository extends JpaRepository<VehicleService, Integer> {

    // Retorna includeAll os vínculos habilitados de um veículo
    List<VehicleService> findByVehicleVehicleIdAndIsLicensedTrue(Integer vehicleId);

    // Retorna includeAll os vínculos de um veículo (habilitados ou não)
    List<VehicleService> findByVehicleVehicleId(Integer vehicleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ServiceVehicle serviceVehicle WHERE serviceVehicle.vehicle.vehicleId = :vehicleId")
    void deleteByVehicleVehicleId(@Param("vehicleId") Integer vehicleId);
}
