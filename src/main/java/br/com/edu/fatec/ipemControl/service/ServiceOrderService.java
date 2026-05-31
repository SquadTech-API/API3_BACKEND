package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ServiceOrderDTO;
import br.com.edu.fatec.ipemControl.dto.ServiceOrderResponseDTO;
import br.com.edu.fatec.ipemControl.entity.ServiceType;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.exception.BusinessRuleException;
import br.com.edu.fatec.ipemControl.repository.ServiceOrderRepository;
import br.com.edu.fatec.ipemControl.repository.ServiceTypeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public ServiceOrderService(ServiceOrderRepository serviceOrderRepository,
                               VehicleRepository vehicleRepository,
                               ServiceTypeRepository serviceTypeRepository) {
        this.serviceOrderRepository = serviceOrderRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public ServiceOrderResponseDTO create(ServiceOrderDTO dto) {
        if (dto.getVehicleId() == null) throw new BusinessRuleException("Vehicle is required.");
        if (dto.getServiceTypeId() == null) throw new BusinessRuleException("Service type is required.");

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));

        ServiceType serviceType = serviceTypeRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Service type not found."));

        br.com.edu.fatec.ipemControl.entity.ServiceOrder serviceOrder = new br.com.edu.fatec.ipemControl.entity.ServiceOrder();
        serviceOrder.setVehicle(vehicle);
        serviceOrder.setServiceType(serviceType);
        serviceOrder.setObservation(dto.getObservations());

        serviceOrder = serviceOrderRepository.save(serviceOrder);

        return mapToDTO(serviceOrder);
    }

    public List<ServiceOrderResponseDTO> findAll() {
        return serviceOrderRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ServiceOrderResponseDTO mapToDTO(br.com.edu.fatec.ipemControl.entity.ServiceOrder serviceOrder) {
        return new ServiceOrderResponseDTO(
                serviceOrder.getServiceOrderId(),
                serviceOrder.getVehicle().getLicensePlate(),
                serviceOrder.getVehicle().getModel(),
                serviceOrder.getServiceType().getServiceName(),
                serviceOrder.getStatus(),
                serviceOrder.getOpeningDate(),
                serviceOrder.getObservation()
        );
    }
}
