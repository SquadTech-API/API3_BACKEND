package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Fueling;
import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import br.com.edu.fatec.IPEMControl.Entities.Vehicle;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RelatorioVeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    /**
     * CORRIGIDO: antes retornava dados hardcoded (Fiat Uno, ABC-1234...).
     * Agora busca dados reais do banco a partir do idVeiculo.
     */
    public RelatorioVeiculoDTO gerarRelatorioVeiculo(Integer idVeiculo) {

        Vehicle vehicle = veiculoRepository.findById(idVeiculo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado: " + idVeiculo));

        // Total de saídas concluídas
        List<RegistroSaida> saidasConcluidas = registroSaidaRepository
                .findByVeiculoIdVeiculoAndDataHoraSaidaBetween(
                        idVeiculo,
                        LocalDateTime.now().minusYears(5),
                        LocalDateTime.now()
                );

        int totalSaidas = saidasConcluidas.size();

        // KM total rodado somando kmRodados de todas as saídas concluídas
        BigDecimal kmRodado = saidasConcluidas.stream()
                .filter(s -> s.getKmRodados() != null)
                .map(RegistroSaida::getKmRodados)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Litros totais abastecidos
        List<Fueling> fuelings = abastecimentoRepository
                .findByRegistroSaidaVeiculoIdVeiculoOrderByDataHoraDesc(idVeiculo);

        BigDecimal totalLitros = fuelings.stream()
                .filter(a -> a.getLitersAmount() != null)
                .map(Fueling::getLitersAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Consumo médio km/L
        double consumoMedio = 0.0;
        if (totalLitros.compareTo(BigDecimal.ZERO) > 0) {
            consumoMedio = kmRodado.divide(totalLitros, 2, RoundingMode.HALF_UP).doubleValue();
        }

        RelatorioVeiculoDTO dto = new RelatorioVeiculoDTO();
        dto.setPrefixo(vehicle.getPrefix());
        dto.setPlaca(vehicle.getLicensePlate());
        dto.setMarca(vehicle.getBrand());
        dto.setModelo(vehicle.getModel());
        dto.setAno(vehicle.getYear());
        dto.setCombustivel(vehicle.getFuelType());
        dto.setKmRodado(kmRodado.doubleValue());
        dto.setConsumoMedio(consumoMedio);
        dto.setTotalSaidas(totalSaidas);

        return dto;
    }
}