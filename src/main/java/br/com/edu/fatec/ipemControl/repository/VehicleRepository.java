package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
}