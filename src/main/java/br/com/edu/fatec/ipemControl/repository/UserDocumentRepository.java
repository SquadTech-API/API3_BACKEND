package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, Integer> {

    List<UserDocument> findByUserRegistration(Integer registration);

    // Contagens usadas em TechnicianReportService e DocumentService
    long countByUserRegistration(Integer registration);
    long countByUserRegistrationAndReadTrue(Integer registration);
    long countByUserRegistrationAndDownloadedTrue(Integer registration);
}