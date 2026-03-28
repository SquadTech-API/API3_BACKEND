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
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setEmail(dto.getEmail());
        usuario.setCargo(dto.getCargo());
        usuario.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        usuario.setAtivo(true);
        return repository.save(usuario);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }



    public List<Usuario> listarTodos() {
        return repository.findAll();
    }
    public Optional<Usuario> buscarPorMatricula(Integer matricula) {
        return repository.findByMatricula(matricula);

    }
    public LoginRespostaDTO autenticar(String email, String senha) {
        Optional<Usuario> optUsuario = repository.findByEmail(email);
        if (optUsuario.isEmpty()) return null;

        Usuario usuario = optUsuario.get();
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) return null;

        return new LoginRespostaDTO(
                usuario.getMatricula(),
                usuario.getNomeCompleto(),
                usuario.getCargo(),
                usuario.getEmail()
        );
    }
    public boolean atualizarSenha(AtualizarSenhaDTO dto) {
        Optional<Usuario> optUsuario = repository.findByEmail(dto.getEmail());
        if (optUsuario.isEmpty()) return false;

        Usuario usuario = optUsuario.get();

        // verifica se a senha atual está correta antes de atualizar
        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenhaHash())) return false;

        usuario.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));
        repository.save(usuario);
        return true;
    }
}

