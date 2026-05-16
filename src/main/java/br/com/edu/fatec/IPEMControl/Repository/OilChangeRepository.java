package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, Integer> {

    // Total de trocas de óleo vinculadas a saídas do técnico
    long countByRegistroSaidaUsuarioMatricula(Integer registration);

    // Última troca do técnico (para relatório de técnicos)
    Optional<OilChange> findTopByRegistroSaidaUsuarioMatriculaOrderByCreatedAtDesc(Integer registration);

    // Última troca de um veículo específico
    @Query("SELECT t FROM OilChange t WHERE t.veiculo.idVeiculo = :idVeiculo ORDER BY t.createdAt DESC")
    Optional<OilChange> buscarUltimaPorVeiculo(@Param("vehicleId") Integer vehicleId);

    // NOVO: lista trocas por veículo ordenadas da mais recente
    // Usado pelo TrocaOleoService.listarPorVeiculo()
    List<OilChange> findByVeiculoIdVeiculoOrderByCreatedAtDesc(Integer vehicleId);
    
    // Trocas após uma determinada data (para relatório de abastecimento)
    List<OilChange> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime startDate);

    // CORRIGIDO: busca trocas vinculadas a uma saída específica
    Optional<OilChange> findTopByRegistroSaidaIdSaidaOrderByCreatedAtDesc(Integer exitId);
}