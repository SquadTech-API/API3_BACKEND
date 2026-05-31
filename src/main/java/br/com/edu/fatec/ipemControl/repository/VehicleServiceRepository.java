package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.VehicleService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface VehicleServiceRepository extends JpaRepository<VehicleService, Integer> {

    // Vínculos habilitados de uma viatura
    List<VehicleService> findByVehicleIdAndEnabledTrue(Integer vehicleId);

    // Todos os vínculos de uma viatura (habilitados ou não)
    List<VehicleService> findByVehicleId(Integer vehicleId);

    // Remove todos os vínculos de uma viatura (usado no sync)
    @Modifying
    @Transactional
    @Query("DELETE FROM VehicleService vs WHERE vs.vehicle.id = :vehicleId")
    void deleteByVehicleId(@Param("vehicleId") Integer vehicleId);
}