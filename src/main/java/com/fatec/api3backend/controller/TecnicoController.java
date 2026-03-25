package com.fatec.api3backend.controller;

import com.fatec.api3backend.model.Tecnico;
import com.fatec.api3backend.repository.TecnicoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tecnicos")
public class TecnicoController {

    @Autowired
    private TecnicoRepository tecnicoRepository;

    // GET - listar todos
    @GetMapping
    public List<Tecnico> listarTodos() {
        return tecnicoRepository.findAll();
    }

    // GET por CPF
    @GetMapping("/{cpf}")
    public ResponseEntity<?> buscarPorCpf(@PathVariable String cpf) {
        Tecnico tecnico = tecnicoRepository.findByCpf(cpf);

        if (tecnico == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Técnico não encontrado");
        }

        return ResponseEntity.ok(tecnico);
    }

    // POST
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid Tecnico tecnico) {

        if (tecnicoRepository.findByCpf(tecnico.getCpf()) != null) {
            return ResponseEntity.badRequest()
                    .body("Já existe um técnico com esse CPF");
        }

        Tecnico novo = tecnicoRepository.save(tecnico);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }
}