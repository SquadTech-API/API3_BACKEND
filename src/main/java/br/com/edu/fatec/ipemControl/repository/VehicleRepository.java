package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Todas as viaturas ativas
    List<Vehicle> findByActiveTrue();

    // Viaturas ativas e disponíveis
    List<Vehicle> findByActiveTrueAndAvailableTrue();

    // Viaturas ativas e em uso
    List<Vehicle> findByActiveTrueAndAvailableFalse();

    // Busca por prefixo
    Optional<Vehicle> findByPrefix(String prefix);

    // Busca por placa
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    // Viaturas com alerta de troca de óleo pendente
    List<Vehicle> findByActiveTrueAndOilChangeAlertSentFalse();
}