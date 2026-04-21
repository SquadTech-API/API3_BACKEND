package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AtualizarSenhaDTO;
import br.com.edu.fatec.IPEMControl.DTO.LoginRespostaDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsuarioDTO;
import br.com.edu.fatec.IPEMControl.Entities.Usuario;
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

    public Usuario salvar(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setMatricula(dto.getMatricula());
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

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorMatricula(Integer matricula) {
        return repository.findByMatricula(matricula);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    public LoginRespostaDTO autenticar(String email, String senha) {
        Optional<Usuario> optional = repository.findByEmail(email);
        if (optional.isEmpty()) return null;

        Usuario usuario = optional.get();
        if (!usuario.getColaboradorAtivo()) return null;
        if (!passwordEncoder.matches(senha, usuario.getSenha())) return null;

        return new LoginRespostaDTO(
                usuario.getMatricula(),
                usuario.getNome(),
                usuario.getCargo(),
                usuario.getEmail(),
                usuario.getTipoUsuario().name()
        );
    }

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