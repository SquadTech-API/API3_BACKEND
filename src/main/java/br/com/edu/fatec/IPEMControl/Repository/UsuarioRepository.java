package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
        Optional<Usuario> findByEmail(String email);
        Optional<Usuario> findByMatricula(Integer matricula);
    }

