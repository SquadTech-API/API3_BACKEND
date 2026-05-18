package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleDashboardResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.VehicleDashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/Vehicle")
@CrossOrigin("*")
public class DashboardVehicleController {

    private final VehicleDashboardService service;

    public DashboardVehicleController(VehicleDashboardService service) {
        this.service = service;
    }

    @GetMapping
    public VehicleDashboardResponseDTO findDashboard() {
        return service.findDashboard();
    }
}