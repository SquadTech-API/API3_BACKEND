package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.ActiveUsageDTO;
import br.com.edu.fatec.IPEMControl.DTO.VehicleUsageDTO;
import br.com.edu.fatec.IPEMControl.Service.UsoVeiculoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle-usages")
public class VehicleUsageController {

    private final UsoVeiculoService vehicleUsageService;

    public VehicleUsageController(UsoVeiculoService vehicleUsageService) {
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