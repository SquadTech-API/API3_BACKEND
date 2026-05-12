package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderDTO;
import br.com.edu.fatec.IPEMControl.DTO.ServiceOrderResponseDTO;
import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Exception.RegraDeNegocioException;
import br.com.edu.fatec.IPEMControl.Repository.OrdemServicoRepository;
import br.com.edu.fatec.IPEMControl.Repository.TipoServicoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceOrder {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    public ServiceOrderResponseDTO criar(ServiceOrderDTO dto) {
        if (dto.getVehicleId() == null) throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getServiceTypeId() == null) throw new RegraDeNegocioException("Informe o type de serviço.");

        Vehicle vehicle = veiculoRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        ServiceType serviceType = tipoServicoRepository.findById(dto.getServiceTypeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        br.com.edu.fatec.IPEMControl.Entities.ServiceOrder ordem = new br.com.edu.fatec.IPEMControl.Entities.ServiceOrder();
        ordem.setVehicle(vehicle);
        ordem.setServiceType(serviceType);
        ordem.setObservation(dto.getObservations());

        ordem = ordemServicoRepository.save(ordem);

        return mapearParaDTO(ordem);
    }

    public List<ServiceOrderResponseDTO> listarTodas() {
        return ordemServicoRepository.findAll().stream()
                .map(this::mapearParaDTO)
                .collect(Collectors.toList());
    }

    private ServiceOrderResponseDTO mapearParaDTO(br.com.edu.fatec.IPEMControl.Entities.ServiceOrder ordem) {
        return new ServiceOrderResponseDTO(
                ordem.getServiceOrderId(),
                ordem.getVehicle().getLicensePlate(),
                ordem.getVehicle().getModel(),
                ordem.getServiceType().getNomeServico(),
                ordem.getStatus(),
                ordem.getOpeningDate(),
                ordem.getObservation()
        );
    }
}