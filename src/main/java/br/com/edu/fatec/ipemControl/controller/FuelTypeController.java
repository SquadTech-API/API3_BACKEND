package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.FuelTypeDTO;
import br.com.edu.fatec.ipemControl.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fuel-types")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    // GET /fuel-types
    @GetMapping
    public ResponseEntity<List<FuelTypeDTO>> findAll() {
        return ResponseEntity.ok(fuelTypeService.findAll());
    }

    // GET /fuel-types/active
    @GetMapping("/active")
    public ResponseEntity<List<FuelTypeDTO>> findActive() {
        return ResponseEntity.ok(fuelTypeService.findActive());
    }

    // GET /fuel-types/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FuelTypeDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(fuelTypeService.findById(id));
    }

    // POST /fuel-types (#A14)
    @PostMapping
    public ResponseEntity<FuelTypeDTO> create(@RequestBody FuelTypeDTO dto) {
        return ResponseEntity.status(201).body(fuelTypeService.create(dto));
    }

    // PUT /fuel-types/{id}
    @PutMapping("/{id}")
    public ResponseEntity<FuelTypeDTO> update(
            @PathVariable Integer id,
            @RequestBody FuelTypeDTO dto) {
        return ResponseEntity.ok(fuelTypeService.update(id, dto));
    }

    // PATCH /fuel-types/{id}/toggle
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<FuelTypeDTO> toggle(@PathVariable Integer id) {
        return ResponseEntity.ok(fuelTypeService.toggle(id));
    }
}