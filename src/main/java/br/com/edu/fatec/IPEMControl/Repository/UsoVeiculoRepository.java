package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsoVeiculoRepository extends JpaRepository<UsoVeiculo, Long> {

    boolean existsByVeiculoAndDataFimIsNull(String veiculo);

    List<UsoVeiculo> findByDataFimIsNull();

    @Query("""
        SELECT u.veiculo, COUNT(u), SUM(u.kmPercorrido)
        FROM UsoVeiculo u
        GROUP BY u.veiculo
        ORDER BY COUNT(u) DESC
    """)
    List<Object[]> buscarComparativoViaturas();
}