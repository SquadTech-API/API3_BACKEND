package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.TrocaOleo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrocaOleoRepository extends JpaRepository<TrocaOleo, Integer> {

    /** Total de trocas de óleo vinculadas a saídas deste usuário */
    long countByRegistroSaidaUsuarioMatricula(Integer matricula);

    /** Última troca de óleo do técnico */
    Optional<TrocaOleo> findTopByRegistroSaidaUsuarioMatriculaOrderByCreatedAtDesc(Integer matricula);
}