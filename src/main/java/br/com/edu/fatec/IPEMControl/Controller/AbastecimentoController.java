package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyHistoryDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Service.FuelSupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/abastecimento")
public class AbastecimentoController {

    @Autowired
    private FuelSupplyService fuelSupplyService;

    @PostMapping
    public ResponseEntity<Abastecimento> criar(@RequestBody AbastecimentoDTO dto) {
        return ResponseEntity.status(201).body(fuelSupplyService.salvar(dto));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<FuelSupplyHistoryDTO>> historico(
            @RequestParam(required = false) Integer idVeiculo) {
        return ResponseEntity.ok(fuelSupplyService.listarHistorico(idVeiculo));
    }
}