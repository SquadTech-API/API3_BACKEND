package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.HistoricoUsoDTO;
import br.com.edu.fatec.IPEMControl.Entities.HistoricoUso;
import br.com.edu.fatec.IPEMControl.Repository.HistoricoUsoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoUsoService {

    private final HistoricoUsoRepository historicoUsoRepository;

    public HistoricoUsoService(HistoricoUsoRepository historicoUsoRepository) {
        this.historicoUsoRepository = historicoUsoRepository;
    }

    public List<HistoricoUsoDTO> buscarHistoricoPorVeiculo(Integer veiculoId) {
        List<HistoricoUso> historicos = historicoUsoRepository
                .findByVeiculoIdOrderByDataRegistroDesc(veiculoId);

        return historicos.stream()
                .map(h -> new HistoricoUsoDTO(
                        h.getDescricao(),
                        h.getDataRegistro()
                ))
                .collect(Collectors.toList());
    }
}