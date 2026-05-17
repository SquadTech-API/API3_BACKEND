package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.UsageHistoryCardDTO;
import br.com.edu.fatec.IPEMControl.Service.HistoricoUsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historico")
@CrossOrigin("*")
public class HistoricoUsoController {

    @Autowired
    private HistoricoUsoService service;

    @GetMapping("/veiculo/{id}")
    public ResponseEntity<List<UsageHistoryCardDTO>> getHistorico(@PathVariable Integer id) {
        return ResponseEntity.ok(service.listarHistoricoPorVeiculo(id));
    }
}