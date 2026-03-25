package com.fatec.api3backend.controller;

import com.fatec.api3backend.model.Tecnico;
import com.fatec.api3backend.repository.TecnicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoRepository repository;

    @GetMapping
    public List<Tecnico> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Tecnico salvar(@RequestBody @Valid Tecnico tecnico) {
        return repository.save(tecnico);
    }
}