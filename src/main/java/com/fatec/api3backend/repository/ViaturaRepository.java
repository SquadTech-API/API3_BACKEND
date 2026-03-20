package com.fatec.api3backend.repository;

import com.fatec.api3backend.model.Viatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViaturaRepository extends JpaRepository<Viatura, Long> {
    // Não precisa escrever nada aqui dentro!
    // O JpaRepository já nos dá os métodos de listar, salvar e deletar.
    Viatura findByPlaca(String placa);
}
