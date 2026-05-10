package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.DashboardGraficoDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/comparativo")
    public DashboardGraficoDTO comparativo() {
        return service.buscarComparativo();
    }
}