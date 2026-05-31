package br.com.edu.fatec.ipemControl.repository;

import br.com.edu.fatec.ipemControl.entity.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Integer> {

    List<ServiceOrder> findByVehicleVehicleIdOrderByOpeningDateDesc(Integer vehicleId);

    List<ServiceOrder> findByStatus(String status);
}