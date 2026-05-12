package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleDashboardResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardVehicleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/Vehicle")
@CrossOrigin("*")
public class DashboardVehicleController {

    private final DashboardVehicleService service;

    public DashboardVehicleController(DashboardVehicleService service) {
        this.service = service;
    }

    @GetMapping
    public VehicleDashboardResponseDTO buscarDashboard() {
        return service.buscarDashboard();
    }
}