package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoHistoricoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Service.AbastecimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/abastecimento")
public class AbastecimentoController {

    @Autowired
    private AbastecimentoService abastecimentoService;

    @PostMapping
    public ResponseEntity<Abastecimento> criar(@RequestBody AbastecimentoDTO dto) {
        return ResponseEntity.status(201).body(abastecimentoService.salvar(dto));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<AbastecimentoHistoricoDTO>> historico(
            @RequestParam(required = false) Integer idVeiculo) {
        return ResponseEntity.ok(abastecimentoService.listarHistorico(idVeiculo));
    }
}