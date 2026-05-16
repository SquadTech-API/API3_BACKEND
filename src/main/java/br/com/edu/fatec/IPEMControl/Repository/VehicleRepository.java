package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
}