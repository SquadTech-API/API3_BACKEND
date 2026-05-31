package br.com.edu.fatec.ipemControl.service;

import br.com.edu.fatec.ipemControl.dto.ServiceTypeDTO;
import br.com.edu.fatec.ipemControl.entity.ServiceType;
import br.com.edu.fatec.ipemControl.entity.Vehicle;
import br.com.edu.fatec.ipemControl.entity.VehicleService;
import br.com.edu.fatec.ipemControl.exception.ResourceNotFoundException;
import br.com.edu.fatec.ipemControl.repository.ServiceTypeRepository;
import br.com.edu.fatec.ipemControl.repository.VehicleRepository;
import br.com.edu.fatec.ipemControl.repository.ServiceVehicleRepository;
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

            VehicleService vs = new VehicleService();
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
                .map(VehicleService::getServiceType)
                .filter(ts -> Boolean.TRUE.equals(ts.getLicensed()))
                .map(serviceTypeService::toDTO)
                .toList();
    }
}
