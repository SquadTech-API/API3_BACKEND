package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.HistoricoUso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoUsoRepository extends JpaRepository<HistoricoUso, Integer> {

    List<HistoricoUso> findByVeiculoIdOrderByDataRegistroDesc(Integer veiculoId);
}