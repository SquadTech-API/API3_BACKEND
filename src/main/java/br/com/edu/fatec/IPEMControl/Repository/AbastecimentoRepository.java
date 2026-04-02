package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Integer> {

    @Query("SELECT a FROM Abastecimento a WHERE a.registroSaida.veiculo.idVeiculo = :idVeiculo ORDER BY a.dataHora DESC")
    Optional<Abastecimento> findUltimoByVeiculo(Integer idVeiculo);
}