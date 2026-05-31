package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    // Documentos de uma saída
    List<Document> findByDepartureLogId(Integer departureLogId);
}