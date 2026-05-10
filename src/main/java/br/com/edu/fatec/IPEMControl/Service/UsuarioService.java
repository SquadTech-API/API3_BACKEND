package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AtualizarSenhaDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginRespostaDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsuarioDTO;
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
    public User salvar(UsuarioDTO dto) {
        if (dto.getSenha() == null || dto.getSenha().isBlank())
            throw new RegraDeNegocioException("Senha é obrigatória.");

        User user = new User();
        user.setCpf(dto.getCpf());
        user.setDriverLicenseNumber(dto.getNumeroHabilitacao());
        user.setName(dto.getNome());
        user.setBirthDate(dto.getDataNascimento());
        user.setEmail(dto.getEmail());
        user.setActiveColaborator(dto.getColaboradorAtivo() != null ? dto.getColaboradorAtivo() : true);
        user.setUserType(dto.getUserType() != null ? dto.getUserType() : User.UserType.technician);
        user.setPosition(dto.getCargo());
        user.setTipoHabilitacao(dto.getTipoHabilitacao());
        user.setPassword(passwordEncoder.encode(dto.getSenha()));
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
    // CORRIGIDO: agora retorna tipoHabilitacao e colaboradorAtivo
    public LoginRespostaDTO autenticar(String email, String senha) {
        Optional<User> optional = repository.findByEmail(email);

        if (optional.isEmpty()) return null;

        User user = optional.get();

        if (!Boolean.TRUE.equals(user.getActiveColaborator())) return null;
        if (!passwordEncoder.matches(senha, user.getPassword()))  return null;

        return new LoginRespostaDTO(
                user.getRegistration(),
                user.getName(),
                user.getPosition(),
                user.getEmail(),
                user.getUserType().name(),
                // CORRIGIDO: tipoHabilitacao agora incluído na resposta
                user.getTipoHabilitacao() != null ? user.getTipoHabilitacao().name() : null,
                // CORRIGIDO: colaboradorAtivo incluído para validação no frontend
                user.getActiveColaborator()
        );
    }

    // ── PUT /usuarios/{matricula} — edição de dados ───────────────────────────
    // NOVO: endpoint para edição pelo ADM
    public User atualizar(Integer matricula, UsuarioDTO dto) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank())
            user.setName(dto.getNome());
        if (dto.getCargo() != null)
            user.setPosition(dto.getCargo());
        if (dto.getUserType() != null)
            user.setUserType(dto.getUserType());
        if (dto.getTipoHabilitacao() != null)
            user.setTipoHabilitacao(dto.getTipoHabilitacao());

        return repository.save(user);
    }

    // ── PATCH /usuarios/{matricula}/desativar ─────────────────────────────────
    // NOVO: desativa colaborador
    public User desativar(Integer matricula) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        user.setActiveColaborator(false);
        return repository.save(user);
    }

    // ── PATCH /usuarios/{matricula}/ativar ────────────────────────────────────
    // NOVO: reativa colaborador
    public User ativar(Integer matricula) {
        User user = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        user.setActiveColaborator(true);
        return repository.save(user);
    }

    // ── POST /usuarios/atualizar-senha ────────────────────────────────────────
    public boolean atualizarSenha(AtualizarSenhaDTO dto) {
        Optional<User> optional = repository.findByEmail(dto.getEmail());
        if (optional.isEmpty()) return false;

        User user = optional.get();
        if (!passwordEncoder.matches(dto.getSenhaAtual(), user.getPassword())) return false;

        user.setPassword(passwordEncoder.encode(dto.getNovaSenha()));
        repository.save(user);
        return true;
    }
}