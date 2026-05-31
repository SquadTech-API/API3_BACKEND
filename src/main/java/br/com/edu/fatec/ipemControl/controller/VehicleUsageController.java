package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.ActiveUsageDTO;
import br.com.edu.fatec.ipemControl.dto.VehicleUsageDTO;
import br.com.edu.fatec.ipemControl.service.VehicleUsageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-usages")
public class VehicleUsageController {

    private final VehicleUsageService vehicleUsageService;

    public VehicleUsageController(VehicleUsageService vehicleUsageService) {
        this.vehicleUsageService = vehicleUsageService;
    }

    @PostMapping
    public VehicleUsageDTO register(@RequestBody VehicleUsageDTO dto) {
        return vehicleUsageService.registrar(dto);
    }

    @GetMapping("/in-use")
    public List<ActiveUsageDTO> findInUse() {
        return vehicleUsageService.listarEmUso();
    }
}