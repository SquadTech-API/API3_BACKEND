package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.VeiculoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Repository para VeiculoServico.
 * Antes não existia — o VeiculoServicoService não tinha como persistir vínculos.
 */
@Repository
public interface VeiculoServicoRepository extends JpaRepository<VeiculoServico, Integer> {

    // Retorna todos os vínculos habilitados de um veículo
    List<VeiculoServico> findByVeiculoIdVeiculoAndHabilitadoTrue(Integer idVeiculo);

    // Retorna todos os vínculos de um veículo (habilitados ou não)
    List<VeiculoServico> findByVeiculoIdVeiculo(Integer idVeiculo);

    // Remove todos os vínculos de um veículo (usado na sincronização)
    @Modifying
    @Transactional
    @Query("DELETE FROM VeiculoServico vs WHERE vs.veiculo.idVeiculo = :idVeiculo")
    void deleteByVeiculoIdVeiculo(@Param("idVeiculo") Integer idVeiculo);
}