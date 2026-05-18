package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleDTO;
import br.com.edu.fatec.IPEMControl.DTO.VehicleSummaryDTO;
import br.com.edu.fatec.IPEMControl.Service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleSummaryDTO>> list(
            @RequestParam(required = false, defaultValue = "false") boolean includeAll) {
        return ResponseEntity.ok(vehicleService.findVehicleSummaries(includeAll));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> create(@RequestBody VehicleDTO vehicle) {
        return ResponseEntity.status(201).body(vehicleService.create(vehicle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(
            @PathVariable Integer id,
            @RequestBody VehicleDTO updated) {
        return ResponseEntity.ok(vehicleService.update(id, updated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<VehicleDTO> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleActive(id, false));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<VehicleDTO> activate(@PathVariable Integer id) {
        return ResponseEntity.ok(vehicleService.toggleActive(id, true));
    }
}
