package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RegistroSaidaRepository extends JpaRepository<RegistroSaida, Integer> {

    // Busca o último registro de saída do veículo
    @Query("SELECT r FROM RegistroSaida r WHERE r.veiculo.idVeiculo = :idVeiculo ORDER BY r.dataHoraSaida DESC")
    Optional<RegistroSaida> findUltimoByVeiculo(Integer idVeiculo);
}