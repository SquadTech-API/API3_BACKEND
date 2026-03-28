package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AtualizarSenhaDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginRespostaDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsuarioDTO;
import br.com.edu.fatec.IPEMControl.Entities.Usuario;
import br.com.edu.fatec.IPEMControl.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(service.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{matricula}")
    public ResponseEntity<Usuario> buscar(@PathVariable Integer matricula) {
        return service.buscarPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        LoginRespostaDTO response = service.autenticar(dto.getEmail(), dto.getSenha());
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Email ou senha inválidos.");
    }
    @PostMapping("/atualizar-senha")
    public ResponseEntity<String> atualizarSenha(@RequestBody AtualizarSenhaDTO dto) {
        boolean atualizado = service.atualizarSenha(dto);
        if (atualizado) {
            return ResponseEntity.ok("Senha atualizada com sucesso!");
        }
        return ResponseEntity.status(400).body("Email ou senha atual incorretos.");
    }
}