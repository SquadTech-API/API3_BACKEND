package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.OrdemServicoDTO;
import br.com.edu.fatec.IPEMControl.DTO.OrdemServicoRespostaDTO;
import br.com.edu.fatec.IPEMControl.Entities.ServiceOrder;
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
public class OrdemServicoService {

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    public OrdemServicoRespostaDTO criar(OrdemServicoDTO dto) {
        if (dto.getIdVeiculo() == null) throw new RegraDeNegocioException("Informe o veículo.");
        if (dto.getIdTipoServico() == null) throw new RegraDeNegocioException("Informe o tipo de serviço.");

        Vehicle vehicle = veiculoRepository.findById(dto.getIdVeiculo())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        ServiceType serviceType = tipoServicoRepository.findById(dto.getIdTipoServico())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tipo de serviço não encontrado."));

        ServiceOrder ordem = new ServiceOrder();
        ordem.setVehicle(vehicle);
        ordem.setServiceType(serviceType);
        ordem.setObservation(dto.getObservacoes());

        ordem = ordemServicoRepository.save(ordem);

        return mapearParaDTO(ordem);
    }

    public List<OrdemServicoRespostaDTO> listarTodas() {
        return ordemServicoRepository.findAll().stream()
                .map(this::mapearParaDTO)
                .collect(Collectors.toList());
    }

    private OrdemServicoRespostaDTO mapearParaDTO(ServiceOrder ordem) {
        return new OrdemServicoRespostaDTO(
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