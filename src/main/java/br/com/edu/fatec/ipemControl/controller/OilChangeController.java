package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.OilChangeAlertDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeDTO;
import br.com.edu.fatec.ipemControl.dto.OilChangeResponseDTO;
import br.com.edu.fatec.ipemControl.service.OilChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/oil-changes")
@RequiredArgsConstructor
public class OilChangeController {

    private final OilChangeService oilChangeService;

    // POST /oil-changes
    @PostMapping
    public ResponseEntity<OilChangeResponseDTO> create(@RequestBody OilChangeDTO dto) {
        return ResponseEntity.status(201).body(oilChangeService.save(dto));
    }

    // GET /oil-changes?vehicleId={id}
    @GetMapping
    public ResponseEntity<List<OilChangeResponseDTO>> findAll(
            @RequestParam(required = false) Integer vehicleId) {
        return ResponseEntity.ok(oilChangeService.findByVehicle(vehicleId));
    }

    // GET /oil-changes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OilChangeResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(oilChangeService.findById(id));
    }

    // PUT /oil-changes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<OilChangeResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody OilChangeDTO dto) {
        return ResponseEntity.ok(oilChangeService.update(id, dto));
    }

    // GET /oil-changes/alerts
    @GetMapping("/alerts")
    public ResponseEntity<List<OilChangeAlertDTO>> findAlerts() {
        return ResponseEntity.ok(oilChangeService.findAlerts());
    }
}