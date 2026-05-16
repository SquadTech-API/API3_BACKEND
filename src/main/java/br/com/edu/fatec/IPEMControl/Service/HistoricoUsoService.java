package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoCardDTO;
import br.com.edu.fatec.IPEMControl.Entities.DepartureLog;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Repository.RefuelingRepository;
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
    private RefuelingRepository refuelingRepository;

    public List<HistoricoUsoCardDTO> listarHistoricoPorVeiculo(Integer idVeiculo) {

        // Busca todas as saídas do veículo ordenadas da mais recente
        List<DepartureLog> saidas = registroSaidaRepository
                .findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
                        idVeiculo,
                        java.time.LocalDateTime.now().minusYears(5),
                        java.time.LocalDateTime.now()
                );

        return saidas.stream()
                .sorted((a, b) -> {
                    if (a.getDateTimeDeparture() == null) return 1;
                    if (b.getDateTimeDeparture() == null) return -1;
                    return b.getDateTimeDeparture().compareTo(a.getDateTimeDeparture());
                })
                .map(saida -> {

                    // Motorista
                    String motorista = saida.getUser() != null
                            ? saida.getUser().getName() : "—";

                    // Tipo de serviço
                    String tipoServico = saida.getServiceType() != null
                            ? saida.getServiceType().getNomeServico() : "—";

                    // KM rodados
                    BigDecimal kmRodados = saida.getDrivenKm() != null
                            ? saida.getDrivenKm() : BigDecimal.ZERO;

                    // Verifica se houve abastecimento nessa saída
                    List<Fueling> fuelings =
                            refuelingRepository.findByRegistroSaida(saida);
                    boolean abasteceu = !fuelings.isEmpty();

                    return new HistoricoUsoCardDTO(
                            motorista,
                            saida.getDateTimeDeparture(),
                            tipoServico,
                            kmRodados,
                            abasteceu
                    );
                })
                .collect(Collectors.toList());
    }
}