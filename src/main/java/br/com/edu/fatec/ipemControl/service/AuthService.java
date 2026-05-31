package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.LoginRequestDTO;
import br.com.edu.fatec.ipemControl.dto.LoginResponseDTO;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import br.com.edu.fatec.ipemControl.security.JwtService;
import br.com.edu.fatec.ipemControl.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponseDTO login(LoginRequestDTO dto) {

        // Valida credenciais via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado."));

        if (!user.isActiveEmployee())
            throw new BusinessRuleException("Conta desativada. Contate o administrador.");

        UserPrincipal principal = new UserPrincipal(user);

        // Claims extras embutidos no token — frontend lê sem precisar de outro endpoint
        Map<String, Object> claims = Map.of(
                "registration", user.getRegistration(),
                "userType",     user.getUserType(),
                "fullName",     user.getFullName()
        );

        String token = jwtService.generateToken(principal, claims);

        return new LoginResponseDTO(
                user.getRegistration(),
                user.getFullName(),
                user.getRole(),
                user.getEmail(),
                user.getUserType(),
                user.getLicenseType(),
                user.isActiveEmployee(),
                token
        );
    }
}