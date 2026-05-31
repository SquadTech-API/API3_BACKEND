package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.VehicleDashboardResponseDTO;
import br.com.edu.fatec.ipemControl.service.VehicleDashboardService;
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