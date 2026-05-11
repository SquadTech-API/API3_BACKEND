package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.DocumentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDocumentoRepository extends JpaRepository<DocumentUser, Integer> {

    /** Total de documents atribuídos ao técnico */
    long countByUsuarioMatricula(Integer matricula);

    /** Documentos read (lido = true) */
    long countByUsuarioMatriculaAndLidoTrue(Integer matricula);

    /** Documentos downloaded (baixado = true) */
    long countByUsuarioMatriculaAndBaixadoTrue(Integer matricula);
}