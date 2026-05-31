package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.VehicleDTO;
import br.com.edu.fatec.ipemControl.dto.VehicleSummaryDTO;
import br.com.edu.fatec.ipemControl.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // GET /vehicles?includeAll=false
    @GetMapping
    public ResponseEntity<List<VehicleSummaryDTO>> list(
            @RequestParam(required = false, defaultValue = "false") boolean includeAll) {
        return ResponseEntity.ok(vehicleService.findVehicleSummaries(includeAll));
    }

    // GET /vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    // POST /vehicles
    @PostMapping
    public ResponseEntity<VehicleDTO> create(@RequestBody VehicleDTO dto) {
        return ResponseEntity.status(201).body(vehicleService.create(dto));
    }

    // PUT /vehicles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(
            @PathVariable Integer id,
            @RequestBody VehicleDTO dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    // PATCH /vehicles/{id}/activate
    @PatchMapping("/{id}/activate")
    public ResponseEntity<VehicleDTO> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleActive(id, true));
    }

    // PATCH /vehicles/{id}/deactivate
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<VehicleDTO> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleActive(id, false));
    }
}