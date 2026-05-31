package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.UpdatePasswordDTO;
import br.com.edu.fatec.ipemControl.dto.UserDTO;
import br.com.edu.fatec.ipemControl.dto.UserResponseDTO;
import br.com.edu.fatec.ipemControl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /users
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserDTO dto) {
        return ResponseEntity.status(201).body(userService.save(dto));
    }

    // GET /users
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // GET /users/{registration}
    @GetMapping("/{registration}")
    public ResponseEntity<UserResponseDTO> findByRegistration(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.findByRegistration(registration));
    }

    // PUT /users/{registration} — edição pelo ADM
    @PutMapping("/{registration}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Integer registration,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.update(registration, dto));
    }

    // PUT /users/{registration}/profile — edição pelo próprio usuário (#U02)
    @PutMapping("/{registration}/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @PathVariable Integer registration,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(registration, dto));
    }

    // PATCH /users/{registration}/deactivate
    @PatchMapping("/{registration}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.deactivate(registration));
    }

    // PATCH /users/{registration}/activate
    @PatchMapping("/{registration}/activate")
    public ResponseEntity<UserResponseDTO> activate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.activate(registration));
    }

    // POST /users/change-password (#U02)
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody UpdatePasswordDTO dto) {
        boolean updated = userService.changePassword(dto);
        if (updated) return ResponseEntity.ok("{\"message\":\"Password updated successfully!\"}");
        return ResponseEntity.status(400).body("{\"message\":\"Incorrect current password.\"}");
    }
}