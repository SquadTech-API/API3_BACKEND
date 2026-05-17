package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Entities.ServiceVehicle;
import br.com.edu.fatec.IPEMControl.Exception.ResourceNotFoundException;
import br.com.edu.fatec.IPEMControl.Repository.ServiceTypeRepository;
import br.com.edu.fatec.IPEMControl.Repository.VehicleRepository;
import br.com.edu.fatec.IPEMControl.Repository.ServiceVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Service para gerenciar o vínculo entre veículos e tipos de serviço.
 * Antes não existia — o endpoint /vehicle-servico/sincronizar retornava 404.
 */
@Service
public class VeiculoServicoService {

    @Autowired
    private ServiceVehicleRepository serviceVehicleRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    /**
     * Sincroniza (substitui) os serviços habilitados para um veículo.
     * POST /vehicle-servico/sincronizar/{vehicleId}
     * Body: lista de IDs de tipo_servico habilitados
     */
    @Transactional
    public void sincronizar(Integer idVeiculo, List<Integer> idsTipoServico) {
        Vehicle vehicle = vehicleRepository.findById(idVeiculo)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado."));

        // Remove todos os vínculos atuais do veículo
        serviceVehicleRepository.deleteByVeiculoIdVeiculo(idVeiculo);

        // Cria novos vínculos apenas para os serviços informados
        for (Integer idTipoServico : idsTipoServico) {
            ServiceType serviceType = serviceTypeRepository.findById(idTipoServico)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de serviço não encontrado: " + idTipoServico));

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
    public List<ServiceType> listarServicosAtivosDoVeiculo(Integer idVeiculo) {
        return serviceVehicleRepository
                .findByVeiculoIdVeiculoAndHabilitadoTrue(idVeiculo)
                .stream()
                .map(ServiceVehicle::getServiceType)
                .filter(ts -> Boolean.TRUE.equals(ts.getLicensed()))
                .toList();
    }
}