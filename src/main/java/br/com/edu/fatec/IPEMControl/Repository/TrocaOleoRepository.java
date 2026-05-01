package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.TrocaOleo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrocaOleoRepository extends JpaRepository<TrocaOleo, Integer> {

    /** Total de trocas de óleo vinculadas a saídas deste usuário */
    long countByRegistroSaidaUsuarioMatricula(Integer matricula);

    /** Última troca de óleo do técnico */
    Optional<TrocaOleo> findTopByRegistroSaidaUsuarioMatriculaOrderByCreatedAtDesc(Integer matricula);

    // Última troca de óleo de um veículo específico
    @Query("SELECT t FROM TrocaOleo t " +
            "JOIN t.registroSaida rs " +
            "JOIN rs.veiculo v " +
            "WHERE v.idVeiculo = :idVeiculo " +
            "ORDER BY t.createdAt DESC")
    Optional<TrocaOleo> buscarUltimaPorVeiculo(@Param("idVeiculo") Integer idVeiculo);

    // Trocas de óleo após uma determinada data
    List<TrocaOleo> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dataInicio);

}