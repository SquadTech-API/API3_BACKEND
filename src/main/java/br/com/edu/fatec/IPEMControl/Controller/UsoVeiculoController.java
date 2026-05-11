package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.ActiveUsageDTO;
import br.com.edu.fatec.IPEMControl.DTO.VehicleUsageDTO;
import br.com.edu.fatec.IPEMControl.Service.UsoVeiculoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uso-veiculos")
public class UsoVeiculoController {

    private final UsoVeiculoService service;

    public UsoVeiculoController(UsoVeiculoService service) {
        this.service = service;
    }

    @PostMapping
    public VehicleUsageDTO registrar(@RequestBody VehicleUsageDTO dto) {
        return service.registrar(dto);
    }

    @GetMapping("/em-uso")
    public List<ActiveUsageDTO> listarEmUso() {
        return service.listarEmUso();
    }
}