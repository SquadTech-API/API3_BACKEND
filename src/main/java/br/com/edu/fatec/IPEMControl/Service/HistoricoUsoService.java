package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoCardDTO;
import br.com.edu.fatec.IPEMControl.Entities.HistoricoUso;
import br.com.edu.fatec.IPEMControl.Repository.HistoricoUsoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoUsoService {

    @Autowired
    private HistoricoUsoRepository repository;

    public List<HistoricoUsoCardDTO> listarHistoricoPorVeiculo(Integer idVeiculo) {
        List<HistoricoUso> historicos = repository.findByVeiculoIdVeiculoOrderByDataRegistroDesc(idVeiculo);

        return historicos.stream().map(h -> new HistoricoUsoCardDTO(
                h.getDescricao(),
                h.getDataRegistro(),
                "Serviço Padrão",
                BigDecimal.ZERO,  
                false             // Lógica de abastecimento
        )).collect(Collectors.toList());
    }
}