package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.ServiceVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Repository para ServiceVehicle.
 * Antes não existia — o VeiculoServicoService não tinha como persistir vínculos.
 */
@Repository
public interface ServiceVehicleRepository extends JpaRepository<ServiceVehicle, Integer> {

    // Retorna todos os vínculos habilitados de um veículo
    List<ServiceVehicle> findByVeiculoIdVeiculoAndHabilitadoTrue(Integer vehicleId);

    // Retorna todos os vínculos de um veículo (habilitados ou não)
    List<ServiceVehicle> findByVeiculoIdVeiculo(Integer vehicleId);

    // Remove todos os vínculos de um veículo (usado na sincronização)
    @Modifying
    @Transactional
    @Query("DELETE FROM ServiceVehicle vs WHERE vs.veiculo.vehicleId = :vehicleId")
    void deleteByVeiculoIdVeiculo(@Param("vehicleId") Integer vehicleId);
}