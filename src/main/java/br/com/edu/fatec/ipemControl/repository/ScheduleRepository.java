package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    // Agendamentos do técnico logado
    List<Schedule> findByRequesterRegistrationOrderByScheduledDatetimeDesc(Integer registration);

    // Todos os agendamentos por status (admin)
    List<Schedule> findByStatusOrderByScheduledDatetimeDesc(String status);

    // Todos os agendamentos ordenados por data (admin)
    List<Schedule> findAllByOrderByScheduledDatetimeDesc();

    // Agendamentos pendentes de uma viatura
    List<Schedule> findByVehicleIdAndStatus(Integer vehicleId, String status);
}