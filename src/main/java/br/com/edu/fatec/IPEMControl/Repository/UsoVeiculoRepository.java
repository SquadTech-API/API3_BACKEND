package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsoVeiculoRepository extends JpaRepository<UsoVeiculo, Long> {

    boolean existsByVeiculoAndDataFimIsNull(String veiculo);

    List<UsoVeiculo> findByDataFimIsNull();
}