package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    // Paginado com filtros opcionais (#A16)
    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:registration IS NULL OR a.userRegistration = :registration)
          AND (:entity IS NULL OR a.entity = :entity)
          AND (:action IS NULL OR a.action = :action)
          AND (:from IS NULL OR a.createdAt >= :from)
          AND (:to IS NULL OR a.createdAt <= :to)
        ORDER BY a.createdAt DESC
        """)
    Page<AuditLog> findWithFilters(
            @Param("registration") Integer registration,
            @Param("entity")       String entity,
            @Param("action")       String action,
            @Param("from")         LocalDateTime from,
            @Param("to")           LocalDateTime to,
            Pageable pageable);
}