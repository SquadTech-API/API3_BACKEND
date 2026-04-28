package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.HistoricoUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoUsoRepository extends JpaRepository<HistoricoUso, Integer> {

    List<HistoricoUso> findByVeiculoIdVeiculoOrderByDataRegistroDesc(Integer idVeiculo);

}