package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoHistoricoDTO;
import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoSalvoDTO;
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
    public ResponseEntity<AbastecimentoSalvoDTO> criar(@RequestBody AbastecimentoDTO dto) {
        return ResponseEntity.status(201).body(abastecimentoService.salvar(dto));
    }

    // Retorna histórico de abastecimentos, com filtro opcional por veículo
    @GetMapping("/historico")
    public ResponseEntity<List<AbastecimentoHistoricoDTO>> historico(
            @RequestParam(required = false) Integer idVeiculo) {
        return ResponseEntity.ok(abastecimentoService.buscarHistorico(idVeiculo));
    }
}