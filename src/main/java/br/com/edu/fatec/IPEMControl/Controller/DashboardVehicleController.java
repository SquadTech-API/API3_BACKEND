package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleDashboardResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardVeiculoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/Vehicle")
@CrossOrigin("*")
public class DashboardVehicleController {

    private final DashboardVeiculoService service;

    public DashboardVehicleController(DashboardVeiculoService service) {
        this.service = service;
    }

    @GetMapping
    public VehicleDashboardResponseDTO buscarDashboard() {
        return service.buscarDashboard();
    }
}