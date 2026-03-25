package com.fatec.api3backend.repository;

import com.fatec.api3backend.model.Tecnico; // Isso aqui diz pro Java onde está o Tecnico
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
}