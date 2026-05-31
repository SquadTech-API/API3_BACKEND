package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.DepartureConductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DepartureConductorRepository
        extends JpaRepository<DepartureConductor, DepartureConductor.DepartureConductorId> {

    // Todos os 2os condutores de uma saída
    List<DepartureConductor> findByDepartureLogId(Integer departureLogId);

    // Remove todos os condutores de uma saída
    @Modifying
    @Transactional
    @Query("DELETE FROM DepartureConductor dc WHERE dc.departureLog.id = :departureLogId")
    void deleteByDepartureLogId(@Param("departureLogId") Integer departureLogId);
}