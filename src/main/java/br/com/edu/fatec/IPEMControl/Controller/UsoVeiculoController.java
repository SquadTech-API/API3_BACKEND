package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.UsoVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
import br.com.edu.fatec.IPEMControl.Service.UsoVeiculoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uso-veiculo")
public class UsoVeiculoController {

    private final UsoVeiculoService service;

    public UsoVeiculoController(UsoVeiculoService service) {
        this.service = service;
    }

    @PostMapping
    public UsoVeiculo registrar(@RequestBody UsoVeiculoDTO dto) {
        return service.registrar(dto);
    }
}