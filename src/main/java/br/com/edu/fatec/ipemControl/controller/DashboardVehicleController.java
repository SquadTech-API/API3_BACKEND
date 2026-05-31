package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.VehicleDashboardResponseDTO;
import br.com.edu.fatec.ipemControl.service.VehicleDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/vehicle")
@RequiredArgsConstructor
public class DashboardVehicleController {

    private final VehicleDashboardService vehicleDashboardService;

    // GET /dashboard/vehicle
    @GetMapping
    public VehicleDashboardResponseDTO findDashboard() {
        return vehicleDashboardService.findDashboard();
    }
}