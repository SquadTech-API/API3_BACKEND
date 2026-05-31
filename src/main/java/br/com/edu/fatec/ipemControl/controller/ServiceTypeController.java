package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.service.ServiceTypeService;
import br.com.edu.fatec.ipemControl.service.VehicleServiceSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-types")
@CrossOrigin(origins = "*")
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;
    private final VehicleServiceSyncService vehicleServiceSyncService;

    public ServiceTypeController(ServiceTypeService serviceTypeService,
                                 VehicleServiceSyncService vehicleServiceSyncService) {
        this.serviceTypeService = serviceTypeService;
        this.vehicleServiceSyncService = vehicleServiceSyncService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> list() {
        return ResponseEntity.ok(serviceTypeService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ServiceTypeDTO>> findActive() {
        return ResponseEntity.ok(serviceTypeService.findActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> search(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceTypeService.findById(id));
    }

    @GetMapping("/vehicle/{vehicleId}/active")
    public ResponseEntity<List<ServiceTypeDTO>> findActiveByVehicle(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleServiceSyncService.findActiveServicesByVehicle(vehicleId));
    }

    @PostMapping
    public ResponseEntity<ServiceTypeDTO> create(@RequestBody ServiceTypeDTO serviceType) {
        return ResponseEntity.status(201).body(serviceTypeService.create(serviceType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceTypeDTO> update(
            @PathVariable Integer id,
            @RequestBody ServiceTypeDTO updated) {
        return ResponseEntity.ok(serviceTypeService.update(id, updated));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ServiceTypeDTO> toggle(@PathVariable Integer id) {
        return ResponseEntity.ok(serviceTypeService.toggle(id));
    }
}
