package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CORRIGIDO: adicionado findByLicensedTrue para search apenas serviços ativos.
 * Antes não tinha esse método — TipoServicoController não conseguia filtrar.
 */
@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {

    // NOVO: filtra apenas serviços habilitados
    List<ServiceType> findByLicensedTrue();
}