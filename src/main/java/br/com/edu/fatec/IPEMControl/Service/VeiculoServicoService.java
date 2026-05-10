package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.Entities.ServiceType;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Entities.ServiceVehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.TipoServicoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NOVO: Service para gerenciar o vínculo entre veículos e tipos de serviço.
 * Antes não existia — o endpoint /veiculo-servico/sincronizar retornava 404.
 */
@Service
public class VeiculoServicoService {

    @Autowired
    private VeiculoServicoRepository veiculoServicoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private TipoServicoRepository tipoServicoRepository;

    /**
     * Sincroniza (substitui) os serviços habilitados para um veículo.
     * POST /veiculo-servico/sincronizar/{idVeiculo}
     * Body: lista de IDs de tipo_servico habilitados
     */
    @Transactional
    public void sincronizar(Integer idVeiculo, List<Integer> idsTipoServico) {
        Vehicle vehicle = veiculoRepository.findById(idVeiculo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        // Remove todos os vínculos atuais do veículo
        veiculoServicoRepository.deleteByVeiculoIdVeiculo(idVeiculo);

        // Cria novos vínculos apenas para os serviços informados
        for (Integer idTipoServico : idsTipoServico) {
            ServiceType serviceType = tipoServicoRepository.findById(idTipoServico)
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Tipo de serviço não encontrado: " + idTipoServico));

            ServiceVehicle vs = new ServiceVehicle();
            vs.setVehicle(vehicle);
            vs.setServiceType(serviceType);
            vs.setIsLicensed(true);
            veiculoServicoRepository.save(vs);
        }
    }

    /**
     * Retorna os tipos de serviço ativos habilitados para um veículo.
     * GET /tipo-servicos/veiculo/{idVeiculo}/ativos
     */
    public List<ServiceType> listarServicosAtivosDoVeiculo(Integer idVeiculo) {
        return veiculoServicoRepository
                .findByVeiculoIdVeiculoAndHabilitadoTrue(idVeiculo)
                .stream()
                .map(ServiceVehicle::getServiceType)
                .filter(ts -> Boolean.TRUE.equals(ts.getLicensed()))
                .toList();
    }
}