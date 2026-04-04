package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Service.AbastecimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// CORRIGIDO: era /abastecimento (singular) mas o JS chama /abastecimento (mantido singular)
// Deixamos singular para bater com o abastecimento.js corrigido que chama /abastecimento
@RequestMapping("/abastecimento")
public class AbastecimentoController {

    @Autowired
    private AbastecimentoService abastecimentoService;

    @PostMapping
    public ResponseEntity<Abastecimento> criar(@RequestBody AbastecimentoDTO dto) {
        return ResponseEntity.status(201).body(abastecimentoService.salvar(dto));
    }
}