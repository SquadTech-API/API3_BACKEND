package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.UsoVeiculoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Tecnico;
import br.com.edu.fatec.IPEMControl.Entities.UsoVeiculo;
import br.com.edu.fatec.IPEMControl.Repository.TecnicoRepository;
import br.com.edu.fatec.IPEMControl.Repository.UsoVeiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class UsoVeiculoService {

    private final UsoVeiculoRepository usoRepository;
    private final TecnicoRepository tecnicoRepository;

    public UsoVeiculoService(UsoVeiculoRepository usoRepository,
                             TecnicoRepository tecnicoRepository) {
        this.usoRepository = usoRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public UsoVeiculo registrar(UsoVeiculoDTO dto) {

        Tecnico tecnico = tecnicoRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new RuntimeException("Técnico não encontrado"));

        boolean emUso = usoRepository.existsByVeiculoAndDataFimIsNull(dto.getVeiculo());

        if (emUso) {
            throw new RuntimeException("Veículo já está em uso!");
        }

        UsoVeiculo uso = new UsoVeiculo();
        uso.setTecnico(tecnico);
        uso.setVeiculo(dto.getVeiculo());
        uso.setDataInicio(dto.getDataInicio());

        return usoRepository.save(uso);
    }
}