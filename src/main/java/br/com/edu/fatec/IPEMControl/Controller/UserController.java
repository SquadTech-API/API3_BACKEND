package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.AtualizarSenhaDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginRespostaDTO;
import br.com.edu.fatec.IPEMControl.DTO.UserDTO;
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
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UsuarioService userService;

    // POST /usuarios — cadastrar
    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserDTO dto) {
        return ResponseEntity.status(201).body(userService.salvar(dto));
    }

    // GET /usuarios — listar todos
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.listarTodos());
    }

    // GET /usuarios/{registration}
    @GetMapping("/{registration}")
    public ResponseEntity<User> findByRegistration(@PathVariable Integer registration) {
        return userService.buscarPorMatricula(registration)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /usuarios/login
    // CORRIGIDO: resposta agora inclui tipoHabilitacao e activeEmployee
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        LoginRespostaDTO response = userService.autenticar(dto.getEmail(), dto.getSenha());
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("{\"message\":\"Invalid email or password.\"}");
    }

    // PUT /usuarios/{registration} — NOVO: editar colaborador (ADM)
    @PutMapping("/{registration}")
    public ResponseEntity<User> update(
            @PathVariable Integer registration,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.atualizar(registration, dto));
    }

    // PATCH /usuarios/{registration}/desativar — NOVO
    @PatchMapping("/{registration}/deactivate")
    public ResponseEntity<User> deactivate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.desativar(registration));
    }

    // PATCH /usuarios/{registration}/ativar — NOVO
    @PatchMapping("/{registration}/activate")
    public ResponseEntity<User> activate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.ativar(registration));
    }

    // POST /usuarios/atualizar-password
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody AtualizarSenhaDTO dto) {
        boolean updated = userService.atualizarSenha(dto);
        if (updated) return ResponseEntity.ok("{\"message\":\"Password updated successfully!\"}");
        return ResponseEntity.status(400).body("{\"message\":\"Incorrect email or current password.\"}");
    }
}