package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.DashboardChartDTO;
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
