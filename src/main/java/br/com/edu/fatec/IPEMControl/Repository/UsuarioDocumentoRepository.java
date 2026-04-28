package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.UsuarioDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDocumentoRepository extends JpaRepository<UsuarioDocumento, Integer> {

    /** Total de documentos atribuídos ao técnico */
    long countByUsuarioMatricula(Integer matricula);

    /** Documentos lidos (lido = true) */
    long countByUsuarioMatriculaAndLidoTrue(Integer matricula);

    /** Documentos baixados (baixado = true) */
    long countByUsuarioMatriculaAndBaixadoTrue(Integer matricula);
}