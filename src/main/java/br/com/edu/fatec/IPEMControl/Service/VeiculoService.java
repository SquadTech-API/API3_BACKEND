package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VeiculoResumoDTO;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    public List<VeiculoResumoDTO> listarVeiculosResumido() {
        List<Veiculo> veiculos = veiculoRepository.findAll();

        return veiculos.stream().map(veiculo -> {
            Optional<RegistroSaida> ultimoRegistro = registroSaidaRepository
                    .findUltimoByVeiculo(veiculo.getIdVeiculo());

            boolean disponivel = ultimoRegistro
                    .map(r -> !"em andamento".equalsIgnoreCase(r.getStatus()))
                    .orElse(true);

            var ultimoUso = ultimoRegistro
                    .map(RegistroSaida::getDataHoraSaida)
                    .orElse(null);

            return new VeiculoResumoDTO(
                    veiculo.getIdVeiculo(),
                    veiculo.getModelo(),
                    veiculo.getPrefixo(),
                    veiculo.getNucleoDar(),
                    veiculo.getKmAtual(),
                    ultimoUso,
                    disponivel
                    // ultimoAbastecimento ← removido
            );

        }).toList();
    }
}