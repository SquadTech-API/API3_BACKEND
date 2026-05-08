package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.TrocaOleoDTO;
import br.com.edu.fatec.IPEMControl.Entities.OilChange;
import br.com.edu.fatec.IPEMControl.Service.TrocaOleoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOVO: Controller para trocas de óleo.
 * Antes não existia — todos os endpoints abaixo retornavam 404.
 *
 * Endpoints criados:
 * - POST  /troca-oleo                   — registrar nova troca
 * - GET   /troca-oleo?veiculoId={id}    — listar trocas (por veículo ou todas)
 * - GET   /troca-oleo/{id}              — buscar troca específica
 * - PUT   /troca-oleo/{id}              — editar troca
 */
@RestController
@RequestMapping("/troca-oleo")
@CrossOrigin(origins = "*")
public class TrocaOleoController {

    @Autowired
    private TrocaOleoService trocaOleoService;

    // POST /troca-oleo
    @PostMapping
    public ResponseEntity<OilChange> criar(@RequestBody TrocaOleoDTO dto) {
        return ResponseEntity.status(201).body(trocaOleoService.salvar(dto));
    }

    // GET /troca-oleo?veiculoId={id}
    @GetMapping
    public ResponseEntity<List<OilChange>> listar(
            @RequestParam(required = false) Integer veiculoId) {
        return ResponseEntity.ok(trocaOleoService.listarPorVeiculo(veiculoId));
    }

    // GET /troca-oleo/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OilChange> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(trocaOleoService.buscarPorId(id));
    }

    // PUT /troca-oleo/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OilChange> atualizar(
            @PathVariable Integer id,
            @RequestBody TrocaOleoDTO dto) {
        return ResponseEntity.ok(trocaOleoService.atualizar(id, dto));
    }
}