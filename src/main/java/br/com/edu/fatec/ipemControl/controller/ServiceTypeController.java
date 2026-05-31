package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.service.ServiceTypeService;
import br.com.edu.fatec.ipemControl.service.VehicleServiceSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-types")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;
    private final VehicleServiceSyncService vehicleServiceSyncService;

    // GET /service-types
    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> list() {
        return ResponseEntity.ok(serviceTypeService.findAll());
    }

    // GET /service-types/active
    @GetMapping("/active")
    public ResponseEntity<List<ServiceTypeDTO>> findActive() {
        return ResponseEntity.ok(serviceTypeService.findEnabled());
    }

    // GET /service-types/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceTypeService.findById(id));
    }

    // GET /service-types/vehicle/{vehicleId}/active
    @GetMapping("/vehicle/{vehicleId}/active")
    public ResponseEntity<List<ServiceTypeDTO>> findActiveByVehicle(
            @PathVariable Integer vehicleId) {
        return ResponseEntity.ok(
                vehicleServiceSyncService.findActiveServicesByVehicle(vehicleId));
    }

    // POST /service-types
    @PostMapping
    public ResponseEntity<ServiceTypeDTO> create(@RequestBody ServiceTypeDTO dto) {
        return ResponseEntity.status(201).body(serviceTypeService.create(dto));
    }

    // PUT /service-types/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> update(
            @PathVariable Integer id,
            @RequestBody ServiceTypeDTO dto) {
        return ResponseEntity.ok(serviceTypeService.update(id, dto));
    }

    // PATCH /service-types/{id}/toggle
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ServiceTypeDTO> toggle(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceTypeService.toggle(id));
    }
}