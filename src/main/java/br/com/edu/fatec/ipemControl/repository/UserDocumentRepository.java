package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entities.DocumentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentRepository extends JpaRepository<DocumentUser, Integer> {

    /** Total de documents atribuídos ao técnico */
    long countByUserRegistration(Integer registration);

    /** Documentos read (lido = true) */
    long countByUserRegistrationAndIsReadTrue(Integer registration);

    /** Documentos downloaded (baixado = true) */
    long countByUserRegistrationAndIsDownloadedTrue(Integer registration);
}