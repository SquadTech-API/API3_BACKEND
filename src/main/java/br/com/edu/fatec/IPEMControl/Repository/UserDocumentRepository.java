package br.com.edu.fatec.IPEMControl.Repository;

import br.com.edu.fatec.IPEMControl.Entities.DocumentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentRepository extends JpaRepository<DocumentUser, Integer> {

    /** Total de documents atribuídos ao técnico */
    long countByUsuarioMatricula(Integer registration);

    /** Documentos read (lido = true) */
    long countByUsuarioMatriculaAndLidoTrue(Integer registration);

    /** Documentos downloaded (baixado = true) */
    long countByUsuarioMatriculaAndBaixadoTrue(Integer registration);
}