package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CORRIGIDO: adicionado findByHabilitadoTrue para buscar apenas serviços ativos.
 * Antes não tinha esse método — TipoServicoController não conseguia filtrar.
 */
@Repository
public interface TipoServicoRepository extends JpaRepository<TipoServico, Integer> {

    // NOVO: filtra apenas serviços habilitados
    List<TipoServico> findByHabilitadoTrue();
}