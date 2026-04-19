package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsoVeiculoRepository extends JpaRepository<UsoVeiculo, Long> {

    boolean existsByVeiculoAndDataFimIsNull(String veiculo);

    List<UsoVeiculo> findByDataFimIsNull();

    @Query(value = """
        SELECT 
            veiculo,
            COUNT(*) AS total_usos,
            SUM(TIMESTAMPDIFF(HOUR, data_inicio, data_fim)) AS horas_uso
        FROM uso_veiculo
        WHERE data_fim IS NOT NULL
        GROUP BY veiculo
        ORDER BY total_usos DESC
    """, nativeQuery = true)
    List<Object[]> buscarComparativoUso();
}