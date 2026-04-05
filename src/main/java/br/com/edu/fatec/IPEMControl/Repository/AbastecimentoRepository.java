package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Integer> {

    Optional<Abastecimento> findTopByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(Integer idVeiculo);
}