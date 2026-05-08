package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.UsoVeiculoDTO;
import br.com.edu.fatec.IPEMControl.DTO.UsoAtivoDTO;
import br.com.edu.fatec.IPEMControl.Entities.Technician;
import br.com.edu.fatec.IPEMControl.Entities.VehicleUsage;
import br.com.edu.fatec.IPEMControl.Repository.TecnicoRepository;
import br.com.edu.fatec.IPEMControl.Repository.UsoVeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsoVeiculoService {

    private final UsoVeiculoRepository usoRepository;
    private final TecnicoRepository tecnicoRepository;

    public UsoVeiculoService(UsoVeiculoRepository usoRepository,
                             TecnicoRepository tecnicoRepository) {
        this.usoRepository = usoRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public UsoVeiculoDTO registrar(UsoVeiculoDTO dto) {

        Technician technician = tecnicoRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        boolean emUso = usoRepository.existsByVeiculoAndDataFimIsNull(dto.getVeiculo());

        if (emUso) {
            throw new RuntimeException("Vehicle already in use");
        }

        VehicleUsage uso = new VehicleUsage();
        uso.setTecnico(technician);
        uso.setVehicle(dto.getVeiculo());
        uso.setStartDate(dto.getDataInicio());

        uso = usoRepository.save(uso);

        UsoVeiculoDTO response = new UsoVeiculoDTO();
        response.setTecnicoId(uso.getTecnico().getTechnicianId());
        response.setVeiculo(uso.getVehicle());
        response.setDataInicio(uso.getStartDate());

        return response;
    }

    public List<UsoAtivoDTO> listarEmUso() {

        List<VehicleUsage> ativos = usoRepository.findByDataFimIsNull();

        return ativos.stream()
                .map(u -> new UsoAtivoDTO(
                        u.getTecnico().getName(),
                        u.getVehicle()
                ))
                .collect(Collectors.toList());
    }
}