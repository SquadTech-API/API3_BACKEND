package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleSummaryDTO;
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
 * 1. GET /vehicles?todos=true — ADM vê inativos, técnico não
 * 2. PUT /vehicles/{id} — edição completa de veículo pelo ADM
 * 3. PATCH /vehicles/{id}/desativar — desativa veículo
 * 4. PATCH /vehicles/{id}/ativar — reativa veículo
 * 5. GET /vehicles/{id} — busca individual (já existia)
 * 6. POST /vehicles — cadastrar novo (já existia)
 */
@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VeiculoService vehicleService;

    @Autowired
    private VeiculoRepository vehicleRepository;

    // GET /vehicles?todos=true
    // CORRIGIDO: parâmetro "todos" — quando false filtra veículos ativos
    @GetMapping
    public ResponseEntity<List<VehicleSummaryDTO>> findAll(
            @RequestParam(required = false, defaultValue = "false") boolean all) {
        return ResponseEntity.ok(vehicleService.listarVeiculosResumo(all));
    }

    // GET /vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> findById(@PathVariable Integer id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /vehicles
    @PostMapping
    public ResponseEntity<Vehicle> create(@RequestBody Vehicle vehicle) {
        return ResponseEntity.status(201).body(vehicleRepository.save(vehicle));
    }

    // PUT /vehicles/{id} — NOVO: edição completa de veículo
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> update(
            @PathVariable Integer id,
            @RequestBody Vehicle updatedVehicle) {
        return ResponseEntity.ok(vehicleService.atualizar(id, updatedVehicle));
    }

    // PATCH /vehicles/{id}/desativar — NOVO
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Vehicle> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleAtivo(id, false));
    }

    // PATCH /vehicles/{id}/ativar — NOVO
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Vehicle> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleAtivo(id, true));
    }
}