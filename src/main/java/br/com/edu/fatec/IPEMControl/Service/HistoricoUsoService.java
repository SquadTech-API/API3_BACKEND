package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoCardDTO;
import br.com.edu.fatec.IPEMControl.Repository.AbastecimentoRepository;
import br.com.edu.fatec.IPEMControl.Repository.RegistroSaidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistoricoUsoService {

    @Autowired
    private RegistroSaidaRepository saidaRepository;

    @Autowired
    private AbastecimentoRepository abastecimentoRepository;

    public List<HistoricoUsoCardDTO> listarHistoricoPorVeiculo(Integer idVeiculo) {
        List<HistoricoUsoCardDTO> historicoCompleto = new ArrayList<>();

        // 1. Processa as Saídas (Viagens)
        saidaRepository.findByVeiculoIdVeiculo(idVeiculo).forEach(saida -> {
            historicoCompleto.add(new HistoricoUsoCardDTO(
                    saida.getUsuario() != null ? saida.getUsuario().getNome() : "Motorista N/I",
                    saida.getDataHoraSaida(),
                    saida.getTipoServico() != null ? saida.getTipoServico().getNomeServico() : "Uso Geral",
                    saida.getKmRodados() != null ? saida.getKmRodados() : BigDecimal.ZERO,
                    false
            ));
        });

        // 2. Processa os Abastecimentos
        abastecimentoRepository.findByVeiculoIdVeiculo(idVeiculo).forEach(abs -> {
            String nomeMotorista = "Motorista N/I";
            if (abs.getRegistroSaida() != null && abs.getRegistroSaida().getUsuario() != null) {
                nomeMotorista = abs.getRegistroSaida().getUsuario().getNome();
            }

            historicoCompleto.add(new HistoricoUsoCardDTO(
                    nomeMotorista,
                    abs.getDataHora(),
                    "Abastecimento",
                    abs.getValorTotal() != null ? abs.getValorTotal() : BigDecimal.ZERO,
                    true
            ));
        });

        // 3. Ordenação Corrigida (usando getDataSaida() que existe no seu DTO)
        historicoCompleto.sort((a, b) -> {
            if (a.getDataSaida() == null || b.getDataSaida() == null) return 0;
            return b.getDataSaida().compareTo(a.getDataSaida());
        });

        return historicoCompleto;
    }
}