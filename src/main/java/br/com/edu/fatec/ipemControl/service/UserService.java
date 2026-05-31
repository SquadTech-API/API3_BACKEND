package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.UpdatePasswordDTO;
import br.com.edu.fatec.ipemControl.dto.LoginResponseDTO;
import br.com.edu.fatec.ipemControl.dto.UserDTO;
import br.com.edu.fatec.ipemControl.dto.UserResponseDTO;
import br.com.edu.fatec.ipemControl.entities.User;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── POST /usuarios ────────────────────────────────────────────────────────
    public UserResponseDTO save(UserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new BusinessRuleException("Senha é obrigatória.");

        User user = new User();
        user.setCpf(dto.getCpf());
        user.setDriverLicenseNumber(dto.getLicenseNumber());
        user.setName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setActiveColaborator(dto.getActiveEmployee() != null ? dto.getActiveEmployee() : true);
        user.setUserType(dto.getUserType() != null ? dto.getUserType() : User.UserType.technician);
        user.setPosition(dto.getRole());
        user.setDriverLicenseType(dto.getDriverLicenseType());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return toDTO(repository.save(user));
    }

    // ── GET /usuarios ─────────────────────────────────────────────────────────
    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public UserResponseDTO findByRegistration(Integer registration) {
        return repository.findByRegistration(registration)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    // ── POST /usuarios/login ──────────────────────────────────────────────────
    // CORRIGIDO: agora retorna tipoHabilitacao e activeEmployee
    public LoginResponseDTO authenticate(String email, String senha) {
        Optional<User> optional = repository.findByEmail(email);

        if (optional.isEmpty()) return null;

        User user = optional.get();

        if (!Boolean.TRUE.equals(user.getActiveColaborator())) return null;
        if (!passwordEncoder.matches(senha, user.getPassword()))  return null;

        return new LoginResponseDTO(
                user.getRegistration(),
                user.getName(),
                user.getPosition(),
                user.getEmail(),
                user.getUserType().name(),
                user.getDriverLicenseType() != null ? user.getDriverLicenseType().name() : null,
                user.getActiveColaborator()
        );
    }

    // ── PUT /usuarios/{registration} — edição de data ───────────────────────────
    // NOVO: endpoint para edição pelo ADM
    public UserResponseDTO update(Integer registration, UserDTO dto) {
        User user = repository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());
        if (dto.getRole() != null)
            user.setPosition(dto.getRole());
        if (dto.getUserType() != null)
            user.setUserType(dto.getUserType());
        if (dto.getDriverLicenseType() != null)
            user.setDriverLicenseType(dto.getDriverLicenseType());

        return toDTO(repository.save(user));
    }

    // ── PATCH /usuarios/{registration}/deactivate ─────────────────────────────────
    // NOVO: desativa colaborador
    public UserResponseDTO deactivate(Integer registration) {
        User user = repository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setActiveColaborator(false);
        return toDTO(repository.save(user));
    }

    // ── PATCH /usuarios/{registration}/activate ────────────────────────────────────
    // NOVO: reativa colaborador
    public UserResponseDTO activate(Integer registration) {
        User user = repository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        user.setActiveColaborator(true);
        return toDTO(repository.save(user));
    }

    // ── POST /usuarios/update-password ────────────────────────────────────────
    public boolean updatePassword(UpdatePasswordDTO dto) {
        Optional<User> optional = repository.findByEmail(dto.getEmail());
        if (optional.isEmpty()) return false;

        User user = optional.get();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) return false;

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
        return true;
    }

    private UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setRegistration(user.getRegistration());
        dto.setCpf(user.getCpf());
        dto.setLicenseNumber(user.getDriverLicenseNumber());
        dto.setName(user.getName());
        dto.setBirthDate(user.getBirthDate());
        dto.setEmail(user.getEmail());
        dto.setUserType(user.getUserType());
        dto.setRole(user.getPosition());
        dto.setActiveEmployee(user.getActiveColaborator());
        dto.setDriverLicenseType(user.getDriverLicenseType());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
