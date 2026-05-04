package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AtualizarSenhaDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginRespostaDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsuarioDTO;
import br.com.edu.fatec.IPEMControl.Entities.Usuario;
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
    public Usuario salvar(UsuarioDTO dto) {
        if (dto.getSenha() == null || dto.getSenha().isBlank())
            throw new RegraDeNegocioException("Senha é obrigatória.");

        Usuario usuario = new Usuario();
        usuario.setCpf(dto.getCpf());
        usuario.setNumeroHabilitacao(dto.getNumeroHabilitacao());
        usuario.setNome(dto.getNome());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setEmail(dto.getEmail());
        usuario.setColaboradorAtivo(dto.getColaboradorAtivo() != null ? dto.getColaboradorAtivo() : true);
        usuario.setTipoUsuario(dto.getTipoUsuario() != null ? dto.getTipoUsuario() : Usuario.TipoUsuario.tecnico);
        usuario.setCargo(dto.getCargo());
        usuario.setTipoHabilitacao(dto.getTipoHabilitacao());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return repository.save(usuario);
    }

    // ── GET /usuarios ─────────────────────────────────────────────────────────
    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorMatricula(Integer matricula) {
        return repository.findByMatricula(matricula);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    // ── POST /usuarios/login ──────────────────────────────────────────────────
    // CORRIGIDO: agora retorna tipoHabilitacao e colaboradorAtivo
    public LoginRespostaDTO autenticar(String email, String senha) {
        Optional<Usuario> optional = repository.findByEmail(email);

        if (optional.isEmpty()) return null;

        Usuario usuario = optional.get();

        if (!Boolean.TRUE.equals(usuario.getColaboradorAtivo())) return null;
        if (!passwordEncoder.matches(senha, usuario.getSenha()))  return null;

        return new LoginRespostaDTO(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getCargo(),
                usuario.getEmail(),
                usuario.getTipoUsuario().name(),
                // CORRIGIDO: tipoHabilitacao agora incluído na resposta
                usuario.getTipoHabilitacao() != null ? usuario.getTipoHabilitacao().name() : null,
                // CORRIGIDO: colaboradorAtivo incluído para validação no frontend
                usuario.getColaboradorAtivo()
        );
    }

    // ── PUT /usuarios/{matricula} — edição de dados ───────────────────────────
    // NOVO: endpoint para edição pelo ADM
    public Usuario atualizar(Integer matricula, UsuarioDTO dto) {
        Usuario usuario = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank())
            usuario.setNome(dto.getNome());
        if (dto.getCargo() != null)
            usuario.setCargo(dto.getCargo());
        if (dto.getTipoUsuario() != null)
            usuario.setTipoUsuario(dto.getTipoUsuario());
        if (dto.getTipoHabilitacao() != null)
            usuario.setTipoHabilitacao(dto.getTipoHabilitacao());

        return repository.save(usuario);
    }

    // ── PATCH /usuarios/{matricula}/desativar ─────────────────────────────────
    // NOVO: desativa colaborador
    public Usuario desativar(Integer matricula) {
        Usuario usuario = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        usuario.setColaboradorAtivo(false);
        return repository.save(usuario);
    }

    // ── PATCH /usuarios/{matricula}/ativar ────────────────────────────────────
    // NOVO: reativa colaborador
    public Usuario ativar(Integer matricula) {
        Usuario usuario = repository.findByMatricula(matricula)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        usuario.setColaboradorAtivo(true);
        return repository.save(usuario);
    }

    // ── POST /usuarios/atualizar-senha ────────────────────────────────────────
    public boolean atualizarSenha(AtualizarSenhaDTO dto) {
        Optional<Usuario> optional = repository.findByEmail(dto.getEmail());
        if (optional.isEmpty()) return false;

        Usuario usuario = optional.get();
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) return false;

        usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        repository.save(usuario);
        return true;
    }
}