package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Integer> {

    // Total de documentos atribuídos ao usuário
    long countByUserRegistration(Integer registration);

    // Documentos já lidos
    long countByUserRegistrationAndReadTrue(Integer registration);

    // Documentos já baixados
    long countByUserRegistrationAndDownloadedTrue(Integer registration);
}