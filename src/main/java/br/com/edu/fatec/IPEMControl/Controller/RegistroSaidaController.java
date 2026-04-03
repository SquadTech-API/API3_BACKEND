package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.FecharSaidaDTO;
import br.com.edu.fatec.IPEMControl.DTO.RegistroSaidaDTO;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Service.RegistroSaidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro-saidas")
public class RegistroSaidaController {

    @Autowired
    private RegistroSaidaService registroSaidaService;

    @PostMapping
    public ResponseEntity<RegistroSaida> abrirSaida(@RequestBody RegistroSaidaDTO dto) {
        return ResponseEntity.status(201).body(registroSaidaService.abrirSaida(dto));
    }

    @PatchMapping("/{id}/fechar")
    public ResponseEntity<RegistroSaida> fecharSaida(
            @PathVariable Integer id,
            @RequestBody FecharSaidaDTO dto) {
        return ResponseEntity.ok(registroSaidaService.fecharSaida(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<RegistroSaida>> listar() {
        return ResponseEntity.ok(registroSaidaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroSaida> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(registroSaidaService.buscarPorId(id));
    }
}