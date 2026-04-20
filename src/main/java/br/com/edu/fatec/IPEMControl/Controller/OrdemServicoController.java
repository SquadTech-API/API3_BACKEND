package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.OrdemServicoDTO;
import br.com.edu.fatec.IPEMControl.DTO.OrdemServicoRespostaDTO;
import br.com.edu.fatec.IPEMControl.Service.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordens-servico")
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService ordemServicoService;

    @PostMapping
    public ResponseEntity<OrdemServicoRespostaDTO> criar(@RequestBody OrdemServicoDTO dto) {
        OrdemServicoRespostaDTO resposta = ordemServicoService.criar(dto);
        return ResponseEntity.status(201).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoRespostaDTO>> listar() {
        return ResponseEntity.ok(ordemServicoService.listarTodas());
    }
}