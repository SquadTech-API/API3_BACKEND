package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.DTO.FuelSupplyHistoryDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.FuelSupplyRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbastecimentoService {

    @Autowired
    private FuelSupplyRepository fuelSupplyRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    public Abastecimento salvar(AbastecimentoDTO dto) {
        Optional<RegistroSaida> registroSaida = registroSaidaRepository.findById(dto.getIdSaida());

        if (registroSaida.isEmpty()) {
            throw new RuntimeException("Registro de saída não encontrado!");
        }

        Abastecimento abastecimento = new Abastecimento();
        abastecimento.setRegistroSaida(registroSaida.get());
        abastecimento.setDataHora(dto.getDataHora());
        abastecimento.setTipoCombustivel(dto.getTipoCombustivel());
        abastecimento.setQuantidadeLitros(dto.getQuantidadeLitros());
        abastecimento.setValorTotal(dto.getValorTotal());
        abastecimento.setKmAbastecimento(dto.getKmAbastecimento());
        abastecimento.setPostoNome(dto.getPostoNome());
        abastecimento.setPostoCidade(dto.getPostoCidade());
        abastecimento.setNotaFiscal(dto.getNotaFiscal());

        return fuelSupplyRepository.save(abastecimento);
    }

    public List<FuelSupplyHistoryDTO> listarHistorico(Integer idVeiculo) {
        List<Abastecimento> lista = (idVeiculo != null)
                ? fuelSupplyRepository.findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo)
                : fuelSupplyRepository.findAllByOrderByDataHoraDesc();

        return lista.stream()
                .map(this::mapearParaHistoricoDTO)
                .collect(Collectors.toList());
    }

    private FuelSupplyHistoryDTO mapearParaHistoricoDTO(Abastecimento a) {
        RegistroSaida saida = a.getRegistroSaida();
        Veiculo veiculo = (saida != null) ? saida.getVeiculo() : null;

        return new FuelSupplyHistoryDTO(
                a.getIdAbastecimento(),
                a.getDataHora(),
                a.getTipoCombustivel(),
                a.getQuantidadeLitros(),
                a.getValorTotal(),
                a.getKmAbastecimento(),
                a.getPostoNome(),
                a.getPostoCidade(),
                a.getNotaFiscal(),
                veiculo != null ? veiculo.getIdVeiculo() : null,
                veiculo != null ? veiculo.getModelo()    : null,
                veiculo != null ? veiculo.getPrefixo()   : null,
                veiculo != null ? veiculo.getPlaca()     : null
        );
    }
}