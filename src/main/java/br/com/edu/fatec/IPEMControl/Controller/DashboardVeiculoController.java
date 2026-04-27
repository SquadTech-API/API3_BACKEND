package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.DashboardVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardVeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/veiculos")
@CrossOrigin("*")
public class DashboardVeiculoController {

    @Autowired
    private DashboardVeiculoService service;

    @GetMapping
    public DashboardVeiculoDTO buscarDashboard() {
        return service.buscarDashboard();
    }
}