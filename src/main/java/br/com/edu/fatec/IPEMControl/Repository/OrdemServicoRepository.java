package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findByVeiculoIdVeiculoOrderByDataAberturaDesc(Integer idVeiculo);

    List<OrdemServico> findByStatus(String status);
}