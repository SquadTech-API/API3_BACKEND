package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.DashboardComparativoDTO;
import br.com.edu.fatec.IPEMControl.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/comparativo")
    public List<DashboardComparativoDTO> comparativo() {
        return dashboardService.buscarComparativo();
    }
}