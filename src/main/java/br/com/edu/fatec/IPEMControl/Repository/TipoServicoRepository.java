package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CORRIGIDO: adicionado findByHabilitadoTrue para buscar apenas serviços ativos.
 * Antes não tinha esse método — TipoServicoController não conseguia filtrar.
 */
@Repository
public interface TipoServicoRepository extends JpaRepository<ServiceType, Integer> {

    // NOVO: filtra apenas serviços habilitados
    List<ServiceType> findByHabilitadoTrue();
}