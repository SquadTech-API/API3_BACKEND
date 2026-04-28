package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.*;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class DashboardVeiculoService {

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    public DashboardVeiculoDTO buscarDashboard() {

        List<GraficoKmItemDTO> topSemana = registroSaidaRepository.buscarTop5KmSemana()
                .stream()
                .map(obj -> new GraficoKmItemDTO(
                        ((Number) obj[0]).intValue(),
                        (String) obj[1],
                        ((Number) obj[2]).doubleValue()
                ))
                .toList();

        if (topSemana.isEmpty()) {
            return new DashboardVeiculoDTO(Map.of("semana", new ArrayList<>()), null);
        }

        Integer idVeiculoPadrao = topSemana.get(0).getId();

        VeiculoDashboardDTO veiculoPadrao = montarVeiculo(idVeiculoPadrao);

        return new DashboardVeiculoDTO(
                Map.of("semana", topSemana),
                veiculoPadrao
        );
    }

    private VeiculoDashboardDTO montarVeiculo(Integer idVeiculo) {

        Veiculo v = veiculoRepository.findById(idVeiculo).orElseThrow();

        Double gasto = abastecimentoRepository.totalGastoSemana(idVeiculo);
        Double litros = abastecimentoRepository.totalLitrosSemana(idVeiculo);
        Double km = registroSaidaRepository.totalKmSemana(idVeiculo);
        Long saidas = registroSaidaRepository.totalSaidasSemana(idVeiculo);

        Double consumo = (litros != null && litros > 0) ? km / litros : 0.0;

        DadosVeiculoDashboardDTO dados =
                new DadosVeiculoDashboardDTO(gasto, litros, km, saidas, consumo);

        return new VeiculoDashboardDTO(
                v.getIdVeiculo(),
                v.getModelo(),
                v.getPrefixo(),
                Map.of("semana", dados),
                new ManutencaoVeiculoDTO(
                        v.getKmAtual() != null ? v.getKmAtual().doubleValue() : 0.0,
                        100000.0
                )
        );
    }
}