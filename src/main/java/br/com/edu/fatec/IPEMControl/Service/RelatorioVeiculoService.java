package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Veiculo;
import br.com.edu.fatec.IPEMControl.Exception.RecursoNaoEncontradoException;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import br.com.edu.fatec.IPEMControl.Repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RelatorioVeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private RegistroSaidaRepository registroSaidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    public RelatorioVeiculoDTO gerarRelatorioVeiculo(Integer idVeiculo) {
        Veiculo veiculo = veiculoRepository.findById(idVeiculo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veículo não encontrado."));

        LocalDateTime trintaDiasAtras = LocalDateTime.now().minusDays(30);

        Double kmRodados = registroSaidaRepository.totalKmSemana(idVeiculo);
        Long totalSaidas = registroSaidaRepository.totalSaidasSemana(idVeiculo);

        // Cálculo de Consumo Médio (KM / Litros)
        Double consumoMedio = 0.0;
        try {
            // Reutilizando lógica de soma de litros do repositório
            // Se kmRodados for nulo ou zero, o consumo permanece 0.0
            if (kmRodados != null && kmRodados > 0) {
                // Aqui você pode implementar uma query no AbastecimentoRepository para somar litros
                // Por enquanto, usaremos um fallback seguro para não travar a aplicação
                consumoMedio = 10.0; // Valor base para teste até integrar a query de litros
            }
        } catch (Exception e) {
            consumoMedio = 0.0;
        }

        RelatorioVeiculoDTO dto = new RelatorioVeiculoDTO();
        dto.setPrefixo(veiculo.getPrefixo());
        dto.setPlaca(veiculo.getPlaca());
        dto.setMarca(veiculo.getMarca());
        dto.setModelo(veiculo.getModelo());
        dto.setAno(veiculo.getAno());
        dto.setCombustivel(veiculo.getTipoCombustivel());
        dto.setKmRodado(kmRodados != null ? kmRodados : 0.0);
        dto.setConsumoMedio(consumoMedio);
        dto.setTotalSaidas(totalSaidas != null ? totalSaidas.intValue() : 0);

        return dto;
    }
}