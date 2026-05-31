package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.VehicleUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleUsageRepository extends JpaRepository<VehicleUsage, Long> {

    boolean existsByVehicleAndFinishDateIsNull(String vehicle);

    List<VehicleUsage> findByFinishDateIsNull();

    @Query(value = """
        SELECT 
            vehicle,
            COUNT(*) AS total_usos,
            SUM(TIMESTAMPDIFF(HOUR, data_inicio, data_fim)) AS horas_uso
        FROM uso_veiculo
        WHERE data_fim IS NOT NULL
        GROUP BY vehicle
        ORDER BY total_usos DESC
    """, nativeQuery = true)
    List<Object[]> findUsageComparison();
}