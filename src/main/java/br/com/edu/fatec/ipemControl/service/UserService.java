package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.UpdatePasswordDTO;
import br.com.edu.fatec.ipemControl.dto.UserDTO;
import br.com.edu.fatec.ipemControl.dto.UserResponseDTO;
import br.com.edu.fatec.ipemControl.entity.User;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ── POST /users ───────────────────────────────────────────────
    public UserResponseDTO save(UserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new BusinessRuleException("Senha é obrigatória.");

        User user = new User();
        user.setCpf(dto.getCpf());
        user.setDriverLicenseNumber(dto.getLicenseNumber());
        user.setFullName(dto.getName());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setActiveEmployee(dto.getActiveEmployee() != null ? dto.getActiveEmployee() : true);
        user.setUserType(dto.getUserType() != null ? dto.getUserType() : "technician");
        user.setRole(dto.getRole());
        user.setLicenseType(dto.getLicenseType());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        return toDTO(userRepository.save(user));
    }

    // ── GET /users ────────────────────────────────────────────────
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    // ── GET /users/{registration} ─────────────────────────────────
    public UserResponseDTO findByRegistration(Integer registration) {
        return userRepository.findByRegistration(registration)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    public Optional<User> findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ── PUT /users/{registration} — edição pelo ADM ───────────────
    public UserResponseDTO update(Integer registration, UserDTO dto) {
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setFullName(dto.getName());
        if (dto.getRole() != null)
            user.setRole(dto.getRole());
        if (dto.getUserType() != null)
            user.setUserType(dto.getUserType());
        if (dto.getLicenseType() != null)
            user.setLicenseType(dto.getLicenseType());
        if (dto.getEmail() != null && !dto.getEmail().isBlank())
            user.setEmail(dto.getEmail());

        return toDTO(userRepository.save(user));
    }

    // ── PUT /users/{registration}/profile — edição pelo próprio (#U02) ──
    public UserResponseDTO updateProfile(Integer registration, UserDTO dto) {
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setFullName(dto.getName());
        if (dto.getRole() != null)
            user.setRole(dto.getRole());
        if (dto.getLicenseType() != null)
            user.setLicenseType(dto.getLicenseType());

        // Não permite alterar userType nem activeEmployee pelo próprio usuário
        return toDTO(userRepository.save(user));
    }

    // ── PATCH /users/{registration}/deactivate ────────────────────
    public UserResponseDTO deactivate(Integer registration) {
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        user.setActiveEmployee(false);
        return toDTO(userRepository.save(user));
    }

    // ── PATCH /users/{registration}/activate ──────────────────────
    public UserResponseDTO activate(Integer registration) {
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        user.setActiveEmployee(true);
        return toDTO(userRepository.save(user));
    }

    // ── POST /users/change-password (#U02) ────────────────────────
    public boolean changePassword(UpdatePasswordDTO dto) {
        Optional<User> optional = userRepository.findByEmail(dto.getEmail());
        if (optional.isEmpty()) return false;

        User user = optional.get();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash()))
            return false;

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    // ── Mapeamento ────────────────────────────────────────────────
    public UserResponseDTO toDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setRegistration(user.getRegistration());
        dto.setCpf(user.getCpf());
        dto.setLicenseNumber(user.getDriverLicenseNumber());
        dto.setName(user.getFullName());
        dto.setBirthDate(user.getBirthDate());
        dto.setEmail(user.getEmail());
        dto.setUserType(user.getUserType());
        dto.setRole(user.getRole());
        dto.setActiveEmployee(user.isActiveEmployee());
        dto.setLicenseType(user.getLicenseType());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}