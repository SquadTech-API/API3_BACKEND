package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.User;
import br.com.edu.fatec.IPEMControl.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CORRIGIDO: adicionados endpoints ausentes que o frontend chama:
 * - PUT  /usuarios/{registration}         — editar dados do colaborador (ADM)
 * - PATCH /usuarios/{registration}/desativar — desativar colaborador (ADM)
 * - PATCH /usuarios/{registration}/ativar    — reativar colaborador (ADM)
 */
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // POST /usuarios — cadastrar
    @PostMapping
    public ResponseEntity<User> criar(@RequestBody UserDTO dto) {
        return ResponseEntity.status(201).body(service.salvar(dto));
    }

    // GET /usuarios — listar todos
    @GetMapping
    public ResponseEntity<List<User>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // GET /usuarios/{registration}
    @GetMapping("/{matricula}")
    public ResponseEntity<User> buscar(@PathVariable Integer matricula) {
        return service.buscarPorMatricula(matricula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /usuarios/login
    // CORRIGIDO: resposta agora inclui tipoHabilitacao e activeEmployee
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        LoginResponseDTO response = service.autenticar(dto.getEmail(), dto.getSenha());
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("{\"message\":\"E-mail ou password inválidos.\"}");
    }

    // PUT /usuarios/{registration} — NOVO: editar colaborador (ADM)
    @PutMapping("/{matricula}")
    public ResponseEntity<User> atualizar(
            @PathVariable Integer matricula,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(service.atualizar(matricula, dto));
    }

    // PATCH /usuarios/{registration}/desativar — NOVO
    @PatchMapping("/{matricula}/desativar")
    public ResponseEntity<User> desativar(@PathVariable Integer matricula) {
        return ResponseEntity.ok(service.desativar(matricula));
    }

    // PATCH /usuarios/{registration}/ativar — NOVO
    @PatchMapping("/{matricula}/ativar")
    public ResponseEntity<User> ativar(@PathVariable Integer matricula) {
        return ResponseEntity.ok(service.ativar(matricula));
    }

    // POST /usuarios/atualizar-password
    @PostMapping("/atualizar-senha")
    public ResponseEntity<String> atualizarSenha(@RequestBody UpdatePasswordDTO dto) {
        boolean atualizado = service.atualizarSenha(dto);
        if (atualizado) return ResponseEntity.ok("{\"message\":\"Senha atualizada com sucesso!\"}");
        return ResponseEntity.status(400).body("{\"message\":\"E-mail ou password atual incorretos.\"}");
    }
}