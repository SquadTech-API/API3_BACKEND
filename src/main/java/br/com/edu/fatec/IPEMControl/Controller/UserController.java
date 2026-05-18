package br.com.edu.fatec.IPEMControl.Controller;

import br.com.edu.fatec.IPEMControl.DTO.UpdatePasswordDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginResponseDTO;
import br.com.edu.fatec.IPEMControl.DTO.UserDTO;
import br.com.edu.fatec.IPEMControl.DTO.UserResponseDTO;
import br.com.edu.fatec.IPEMControl.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserDTO dto) {
        return ResponseEntity.status(201).body(userService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{registration}")
    public ResponseEntity<UserResponseDTO> findByRegistration(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.findByRegistration(registration));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        LoginResponseDTO response = userService.authenticate(dto.getEmail(), dto.getPassword());
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("{\"message\":\"Invalid email or password.\"}");
    }

    @PutMapping("/{registration}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable Integer registration,
            @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.update(registration, dto));
    }

    @PatchMapping("/{registration}/deactivate")
    public ResponseEntity<UserResponseDTO> deactivate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.deactivate(registration));
    }

    @PatchMapping("/{registration}/activate")
    public ResponseEntity<UserResponseDTO> activate(@PathVariable Integer registration) {
        return ResponseEntity.ok(userService.activate(registration));
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordDTO dto) {
        boolean updated = userService.updatePassword(dto);
        if (updated) return ResponseEntity.ok("{\"message\":\"Password updated successfully!\"}");
        return ResponseEntity.status(400).body("{\"message\":\"Incorrect email or current password.\"}");
    }
}
