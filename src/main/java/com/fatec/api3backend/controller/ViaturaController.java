package com.fatec.api3backend.controller;

import com.fatec.api3backend.model.Viatura;
import com.fatec.api3backend.repository.ViaturaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viaturas")
public class ViaturaController {

    @Autowired
    private ViaturaRepository viaturaRepository;

    // GET - listar todas
    @GetMapping
    public List<Viatura> listarTodas() {
        return viaturaRepository.findAll();
    }

    // GET por placa
    @GetMapping("/{placa}")
    public ResponseEntity<?> buscarPorPlaca(@PathVariable String placa) {
        Viatura viatura = viaturaRepository.findByPlaca(placa);

        if (viatura == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Viatura não encontrada");
        }

        return ResponseEntity.ok(viatura);
    }

    // POST - AGORA COM VALIDAÇÃO
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody @Valid Viatura viatura) {

        if (viaturaRepository.findByPlaca(viatura.getPlaca()) != null) {
            return ResponseEntity.badRequest()
                    .body("Já existe uma viatura com essa placa");
        }

        Viatura novaViatura = viaturaRepository.save(viatura);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaViatura);
    }
}