package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.AbastecimentoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Abastecimento;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AbastecimentoService {

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

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

        return abastecimentoRepository.save(abastecimento);
    }
}