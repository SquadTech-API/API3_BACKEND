package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.service.VehicleServiceSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-service")
@CrossOrigin(origins = "*")
public class VehicleServiceController {

    private final VehicleServiceSyncService vehicleServiceSyncService;

    public VehicleServiceController(VehicleServiceSyncService vehicleServiceSyncService) {
        this.vehicleServiceSyncService = vehicleServiceSyncService;
    }

    @PostMapping("/sync/{vehicleId}")
    public ResponseEntity<Void> sync(
            @PathVariable Integer vehicleId,
            @RequestBody List<Integer> serviceTypeIds) {
        vehicleServiceSyncService.synchronize(vehicleId, serviceTypeIds);
        return ResponseEntity.ok().build();
    }
}
