package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.DashboardChartDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/comparison")
    public DashboardChartDTO comparison() {
        return service.findComparison();
    }
}
