package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.VehicleUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsageHistoryRepository extends JpaRepository<VehicleUsageHistory, Integer> {

    // Busca o histórico filtrando pelo ID do veículo e ordenando pela data mais recente
    List<VehicleUsageHistory> findByVeiculoIdVeiculoOrderByDataRegistroDesc(Integer vehicleId);
}