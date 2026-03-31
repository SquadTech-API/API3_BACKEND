package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistroSaidaRepository extends JpaRepository<RegistroSaida, Integer> {

    // 🔥 PEGARÁ SOMENTE O ÚLTIMO REGISTRO
    Optional<RegistroSaida> findTopByVeiculoIdVeiculoOrderByDataHoraSaidaDesc(Integer idVeiculo);
}