package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.ServiceTypeDTO;
import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Entities.ServiceVehicle;
import br.com.edu.fatec.IPEMControl.Exception.ResourceNotFoundException;
import br.com.edu.fatec.IPEMControl.Repository.ServiceTypeRepository;
import br.com.edu.fatec.IPEMControl.Repository.VehicleRepository;
import br.com.edu.fatec.IPEMControl.Repository.ServiceVehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Service para gerenciar o vínculo entre veículos e tipos de serviço.
 * Antes não existia — o endpoint /vehicle-servico/synchronize retornava 404.
 */
@Service
public class VehicleServiceSyncService {

    private final ServiceVehicleRepository serviceVehicleRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeService serviceTypeService;

    public VehicleServiceSyncService(ServiceVehicleRepository serviceVehicleRepository,
                                     VehicleRepository vehicleRepository,
                                     ServiceTypeRepository serviceTypeRepository,
                                     ServiceTypeService serviceTypeService) {
        this.serviceVehicleRepository = serviceVehicleRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceTypeRepository = serviceTypeRepository;
        this.serviceTypeService = serviceTypeService;
    }

    /**
     * Sincroniza (substitui) os serviços habilitados para um veículo.
     * POST /vehicle-servico/synchronize/{vehicleId}
     * Body: lista de IDs de tipo_servico habilitados
     */
    @Transactional
    public void synchronize(Integer vehicleId, List<Integer> serviceTypeIds) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        // Remove includeAll os vínculos atuais do veículo
        serviceVehicleRepository.deleteByVehicleVehicleId(vehicleId);

        // Cria novos vínculos apenas para os serviços informados
        for (Integer serviceTypeId : serviceTypeIds) {
            ServiceType serviceType = serviceTypeRepository.findById(serviceTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de serviço não encontrado: " + serviceTypeId));

            ServiceVehicle vs = new ServiceVehicle();
            vs.setVehicle(vehicle);
            vs.setServiceType(serviceType);
            vs.setIsLicensed(true);
            serviceVehicleRepository.save(vs);
        }
    }

    /**
     * Retorna os tipos de serviço ativos habilitados para um veículo.
     * GET /type-services/vehicle/{vehicleId}/ativos
     */
    public List<ServiceTypeDTO> findActiveServicesByVehicle(Integer vehicleId) {
        return serviceVehicleRepository
                .findByVehicleVehicleIdAndIsLicensedTrue(vehicleId)
                .stream()
                .map(ServiceVehicle::getServiceType)
                .filter(ts -> Boolean.TRUE.equals(ts.getLicensed()))
                .map(serviceTypeService::toDTO)
                .toList();
    }
}
