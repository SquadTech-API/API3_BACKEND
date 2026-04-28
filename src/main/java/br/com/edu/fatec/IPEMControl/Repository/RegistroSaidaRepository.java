package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroSaidaRepository extends JpaRepository<RegistroSaida, Integer> {

    Optional<RegistroSaida> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer idVeiculo);

    // ADICIONADO: para o endpoint GET /registro-saidas/ativo?veiculoId=X
    Optional<RegistroSaida> findTopByVeiculoIdVeiculoAndStatusOrderByDataHoraSaidaDesc(
            Integer idVeiculo, String status);

    Optional<RegistroSaida> findTopByUsuarioMatriculaAndStatusOrderByDataHoraSaidaDesc(
            Integer matricula, String status);
}