package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Integer> {

    // Apenas combustíveis ativos
    List<FuelType> findByActiveTrue();

    // Busca por sigla
    Optional<FuelType> findByAbbreviation(String abbreviation);

    // Busca por nome
    Optional<FuelType> findByName(String name);
}