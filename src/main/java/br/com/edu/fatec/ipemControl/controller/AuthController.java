package br.com.edu.fatec.ipemControl.controller;

import br.com.edu.fatec.ipemControl.dto.LoginRequestDTO;
import br.com.edu.fatec.ipemControl.dto.LoginResponseDTO;
import br.com.edu.fatec.ipemControl.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}