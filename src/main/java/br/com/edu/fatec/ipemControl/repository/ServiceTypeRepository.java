package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {

    // Apenas serviços habilitados
    List<ServiceType> findByEnabledTrue();

    // Apenas serviços de troca de óleo
    List<ServiceType> findByIsOilChangeTrue();

    // Serviços habilitados de troca de óleo
    List<ServiceType> findByEnabledTrueAndIsOilChangeTrue();
}