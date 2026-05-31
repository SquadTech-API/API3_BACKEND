package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.entity.ServiceType;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.entity.VehicleService;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.ServiceTypeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceSyncService {

    private final VehicleServiceRepository vehicleServiceRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeService serviceTypeService;

    // ── POST /vehicle-services/synchronize/{vehicleId} ───────────
    @Transactional
    public void synchronize(Integer vehicleId, List<Integer> serviceTypeIds) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        vehicleServiceRepository.deleteByVehicleId(vehicleId);

        for (Integer serviceTypeId : serviceTypeIds) {
            ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de serviço não encontrado: " + serviceTypeId));

            VehicleService vs = new VehicleService();
            vs.setVehicle(vehicle);
            vs.setServiceType(serviceType);
            vs.setEnabled(true);
            vehicleServiceRepository.save(vs);
        }
    }

    // ── GET /vehicle-services/vehicle/{vehicleId}/active ─────────
    public List<ServiceTypeDTO> findActiveServicesByVehicle(Integer vehicleId) {
        return vehicleServiceRepository
                .findByVehicleIdAndEnabledTrue(vehicleId)
                .stream()
                .map(VehicleService::getServiceType)
                .filter(ServiceType::isEnabled)
                .map(serviceTypeService::toDTO)
                .toList();
    }
}