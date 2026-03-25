package com.fatec.api3backend.repository;

import com.fatec.api3backend.model.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    Tecnico findByCpf(String cpf);
}