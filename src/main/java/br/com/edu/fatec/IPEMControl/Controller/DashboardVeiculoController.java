package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.VehicleDashboardResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardVeiculoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/veiculos")
@CrossOrigin("*")
public class DashboardVeiculoController {

    private final DashboardVeiculoService service;

    public DashboardVeiculoController(DashboardVeiculoService service) {
        this.service = service;
    }

    @GetMapping
    public VehicleDashboardResponseDTO buscarDashboard() {
        return service.buscarDashboard();
    }
}