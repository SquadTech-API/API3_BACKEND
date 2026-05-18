package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderDTO;
import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderResponseDTO;
import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.ResourceNotFoundException;
import br.com.edu.fatec.IPEMControl.Exception.BusinessRuleException;
import br.com.edu.fatec.IPEMControl.Repository.ServiceOrderRepository;
import br.com.edu.fatec.IPEMControl.Repository.ServiceTypeRepository;
import br.com.edu.fatec.IPEMControl.Repository.VehicleRepository;
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

        br.com.edu.fatec.IPEMControl.Entities.ServiceOrder serviceOrder = new br.com.edu.fatec.IPEMControl.Entities.ServiceOrder();
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

    private ServiceOrderResponseDTO mapToDTO(br.com.edu.fatec.IPEMControl.Entities.ServiceOrder serviceOrder) {
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
