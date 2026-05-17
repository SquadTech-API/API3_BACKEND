package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FuelingDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelingHistoryDTO;
import br.com.edu.fatec.IPEMControl.DTO.SavedFuelingDTO;
import br.com.edu.fatec.IPEMControl.Service.AbastecimentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/abastecimento")
public class AbastecimentoController {

    private final AbastecimentoService abastecimentoService;

    public AbastecimentoController(AbastecimentoService abastecimentoService) {
        this.abastecimentoService = abastecimentoService;
    }

    // Registra um novo abastecimento
    @PostMapping
    public ResponseEntity<SavedFuelingDTO> criar(@RequestBody FuelingDTO dto) {
        return ResponseEntity.status(201).body(abastecimentoService.salvar(dto));
    }

    // Retorna histórico de refuels, com filtro opcional por veículo
    @GetMapping("/historico")
    public ResponseEntity<List<FuelingHistoryDTO>> historico(
            @RequestParam(required = false) Integer idVeiculo) {
        return ResponseEntity.ok(abastecimentoService.buscarHistorico(idVeiculo));
    }
}