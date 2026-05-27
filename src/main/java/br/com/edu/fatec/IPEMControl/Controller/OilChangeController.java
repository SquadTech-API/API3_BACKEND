package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.OilChangeAlertDTO;
import br.com.edu.fatec.IPEMControl.DTO.OilChangeDTO;
import br.com.edu.fatec.IPEMControl.DTO.OilChangeResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.OilChangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * NOVO: Controller para trocas de óleo.
 * Antes não existia — includeAll os endpoints abaixo retornavam 404.
 *
 * Endpoints criados:
 * - POST  /oilChange-oleo                   — registrar nova oilChange
 * - GET   /oilChange-oleo?veiculoId={id}    — list trocas (por veículo ou todas)
 * - GET   /oilChange-oleo/{id}              — search oilChange específica
 * - PUT   /oilChange-oleo/{id}              — editar oilChange
 */
@RestController
@RequestMapping("/oil-changes")
@CrossOrigin(origins = "*")
public class OilChangeController {

    private final OilChangeService oilChangeService;

    public OilChangeController(OilChangeService oilChangeService) {
        this.oilChangeService = oilChangeService;
    }

    // POST /oilChange-oleo
    @PostMapping
    public ResponseEntity<OilChangeResponseDTO> create(@RequestBody OilChangeDTO dto) {
        return ResponseEntity.status(201).body(oilChangeService.save(dto));
    }

    // GET /oilChange-oleo?veiculoId={id}
    @GetMapping
    public ResponseEntity<List<OilChangeResponseDTO>> findAll(
            @RequestParam(required = false) Integer vehicleId) {
        return ResponseEntity.ok(oilChangeService.findByVehicle(vehicleId));
    }

    // GET /oilChange-oleo/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OilChangeResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(oilChangeService.findById(id));
    }

    // PUT /oilChange-oleo/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OilChangeResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody OilChangeDTO dto) {
        return ResponseEntity.ok(oilChangeService.update(id, dto));
    }
    @GetMapping("/alerts")
    public ResponseEntity<List<OilChangeAlertDTO>> findAlerts() {
        return ResponseEntity.ok(oilChangeService.findAlerts());
    }

}
