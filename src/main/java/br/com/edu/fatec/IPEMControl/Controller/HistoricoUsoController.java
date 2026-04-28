package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoDTO;
import br.com.edu.fatec.IPEMControl.Service.HistoricoUsoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/veiculos")
@CrossOrigin("*")
public class HistoricoUsoController {

    private final HistoricoUsoService service;

    public HistoricoUsoController(HistoricoUsoService service) {
        this.service = service;
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoUsoDTO>> buscarHistorico(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarHistoricoPorVeiculo(id));
    }
}