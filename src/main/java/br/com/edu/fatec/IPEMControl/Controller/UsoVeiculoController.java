package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.UsoAtivoDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsoVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
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
    public UsoVeiculoDTO registrar(@RequestBody UsoVeiculoDTO dto) {
        return service.registrar(dto);
    }

    @GetMapping("/em-uso")
    public List<UsoAtivoDTO> listarEmUso() {
        return service.listarEmUso();
    }
}