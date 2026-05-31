package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
}