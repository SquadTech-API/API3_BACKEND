package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.OilChangeDTO;
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
@RequestMapping("/oil-changes")
@CrossOrigin(origins = "*")
public class OilChangeController {

    @Autowired
    private TrocaOleoService oilChangeService;

    // POST /troca-oleo
    @PostMapping
    public ResponseEntity<OilChange> create(@RequestBody OilChangeDTO dto) {
        return ResponseEntity.status(201).body(oilChangeService.salvar(dto));
    }

    // GET /troca-oleo?veiculoId={id}
    @GetMapping
    public ResponseEntity<List<OilChange>> findAll(
            @RequestParam(required = false) Integer vehicleId) {
        return ResponseEntity.ok(oilChangeService.listarPorVeiculo(vehicleId));
    }

    // GET /troca-oleo/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OilChange> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(oilChangeService.buscarPorId(id));
    }

    // PUT /troca-oleo/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OilChange> update(
            @PathVariable Integer id,
            @RequestBody OilChangeDTO dto) {
        return ResponseEntity.ok(oilChangeService.atualizar(id, dto));
    }
}