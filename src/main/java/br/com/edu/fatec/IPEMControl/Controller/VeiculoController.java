package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VeiculoResumoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import br.com.edu.fatec.IPEMControl.Service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CORRIGIDO: controller unificado (era dividido em VeiculoController + VeiculoAdmController).
 *
 * Correções aplicadas:
 * 1. GET /veiculos?todos=true — ADM vê inativos, técnico não
 * 2. PUT /veiculos/{id} — edição completa de veículo pelo ADM
 * 3. PATCH /veiculos/{id}/desativar — desativa veículo
 * 4. PATCH /veiculos/{id}/ativar — reativa veículo
 * 5. GET /veiculos/{id} — busca individual (já existia)
 * 6. POST /veiculos — cadastrar novo (já existia)
 */
@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private VeiculoRepository veiculoRepository;

    // GET /veiculos?todos=true
    // CORRIGIDO: parâmetro "todos" — quando false filtra veículos ativos
    @GetMapping
    public ResponseEntity<List<VeiculoResumoDTO>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean todos) {
        return ResponseEntity.ok(veiculoService.listarVeiculosResumo(todos));
    }

    // GET /veiculos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> buscarPorId(@PathVariable Integer id) {
        return veiculoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /veiculos
    @PostMapping
    public ResponseEntity<Vehicle> criar(@RequestBody Vehicle vehicle) {
        return ResponseEntity.status(201).body(veiculoRepository.save(vehicle));
    }

    // PUT /veiculos/{id} — NOVO: edição completa de veículo
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> atualizar(
            @PathVariable Integer id,
            @RequestBody Vehicle atualizado) {
        return ResponseEntity.ok(veiculoService.atualizar(id, atualizado));
    }

    // PATCH /veiculos/{id}/desativar — NOVO
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Vehicle> desativar(@PathVariable Integer id) {
        return ResponseEntity.ok(veiculoService.toggleAtivo(id, false));
    }

    // PATCH /veiculos/{id}/ativar — NOVO
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Vehicle> ativar(@PathVariable Integer id) {
        return ResponseEntity.ok(veiculoService.toggleAtivo(id, true));
    }
}