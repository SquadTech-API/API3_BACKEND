package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.LoginResponseDTO;
import br.com.edu.fatec.IPEMControl.DTO.UpdatePasswordDTO;
import br.com.edu.fatec.IPEMControl.DTO.UserDTO;
import br.com.edu.fatec.IPEMControl.Entities.User;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Exception.RegraDeNegocioException;
import br.com.edu.fatec.IPEMControl.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ── POST /usuarios ────────────────────────────────────────────────────────
    public User salvar(UserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new RegraDeNegocioException("Senha é obrigatória.");

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
        return repository.save(user);
    }

    // ── GET /usuarios ─────────────────────────────────────────────────────────
    public List<User> listarTodos() {
        return repository.findAll();
    }

    public Optional<User> buscarPorMatricula(Integer matricula) {
        return repository.findByMatricula(matricula);
    }

    public Optional<User> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    // ── POST /usuarios/login ──────────────────────────────────────────────────
    // CORRIGIDO: agora retorna tipoHabilitacao e activeEmployee
    public LoginResponseDTO autenticar(String email, String senha) {
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
                // CORRIGIDO: tipoHabilitacao agora incluído na resposta
                user.getDriverLicenseType() != null ? user.getDriverLicenseType().name() : null,
                // CORRIGIDO: activeEmployee incluído para validação no frontend
                user.getActiveColaborator()
        );
    }

    // ── PUT /usuarios/{registration} — edição de dados ───────────────────────────
    // NOVO: endpoint para edição pelo ADM
    public User atualizar(Integer matricula, UserDTO dto) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (dto.getName() != null && !dto.getName().isBlank())
            user.setName(dto.getName());
        if (dto.getRole() != null)
            user.setPosition(dto.getRole());
        if (dto.getUserType() != null)
            user.setUserType(dto.getUserType());
        if (dto.getDriverLicenseType() != null)
            user.setDriverLicenseType(dto.getDriverLicenseType());

        return repository.save(user);
    }

    // ── PATCH /usuarios/{registration}/desativar ─────────────────────────────────
    // NOVO: desativa colaborador
    public User desativar(Integer matricula) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        user.setActiveColaborator(false);
        return repository.save(user);
    }

    // ── PATCH /usuarios/{registration}/ativar ────────────────────────────────────
    // NOVO: reativa colaborador
    public User ativar(Integer matricula) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        user.setActiveColaborator(true);
        return repository.save(user);
    }

    // ── POST /usuarios/atualizar-password ────────────────────────────────────────
    public boolean atualizarSenha(UpdatePasswordDTO dto) {
        Optional<User> optional = repository.findByEmail(dto.getEmail());
        if (optional.isEmpty()) return false;

        User user = optional.get();
        if (!passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) return false;

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
        return true;
    }
}