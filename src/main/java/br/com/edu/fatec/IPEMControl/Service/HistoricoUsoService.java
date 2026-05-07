package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoCardDTO;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoUsoService {


    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    public List<HistoricoUsoCardDTO> listarHistoricoPorVeiculo(Integer idVeiculo) {

        // Busca todas as saídas do veículo ordenadas da mais recente
        List<RegistroSaida> saidas = registroSaidaRepository
                .findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
                        idVeiculo,
                        java.time.LocalDateTime.now().minusYears(5),
                        java.time.LocalDateTime.now()
                );

        return saidas.stream()
                .sorted((a, b) -> {
                    if (a.getDataHoraSaida() == null) return 1;
                    if (b.getDataHoraSaida() == null) return -1;
                    return b.getDataHoraSaida().compareTo(a.getDataHoraSaida());
                })
                .map(saida -> {

                    // Motorista
                    String motorista = saida.getUser() != null
                            ? saida.getUser().getName() : "—";

                    // Tipo de serviço
                    String tipoServico = saida.getTipoServico() != null
                            ? saida.getTipoServico().getNomeServico() : "—";

                    // KM rodados
                    BigDecimal kmRodados = saida.getKmRodados() != null
                            ? saida.getKmRodados() : BigDecimal.ZERO;

                    // Verifica se houve abastecimento nessa saída
                    List<Fueling> fuelings =
                            abastecimentoRepository.findByRegistroSaida(saida);
                    boolean abasteceu = !fuelings.isEmpty();

                    return new HistoricoUsoCardDTO(
                            motorista,
                            saida.getDataHoraSaida(),
                            tipoServico,
                            kmRodados,
                            abasteceu
                    );
                })
                .collect(Collectors.toList());
    }
}