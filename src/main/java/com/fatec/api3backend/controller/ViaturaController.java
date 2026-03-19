package com.fatec.api3backend.controller;

import com.fatec.api3backend.model.Viatura;
import com.fatec.api3backend.repository.ViaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viaturas") // Define que este controller cuida da rota /viaturas
public class ViaturaController {

    @Autowired
    private ViaturaRepository repository;

    // Tarefa #17: Listar todas as viaturas cadastradas
    @GetMapping
    public List<Viatura> listarTodas() {
        return repository.findAll();
    }
}