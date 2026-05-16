package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.VehicleUsageDTO;
import br.com.edu.fatec.IPEMControl.DTO.ActiveUsageDTO;
import br.com.edu.fatec.IPEMControl.Entities.Technician;
import br.com.edu.fatec.IPEMControl.Entities.VehicleUsage;
import br.com.edu.fatec.IPEMControl.Repository.TechnicianRepository;
import br.com.edu.fatec.IPEMControl.Repository.UsoVeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsoVeiculoService {

    private final UsoVeiculoRepository usoRepository;
    private final TechnicianRepository technicianRepository;

    public UsoVeiculoService(UsoVeiculoRepository usoRepository,
                             TechnicianRepository technicianRepository) {
        this.usoRepository = usoRepository;
        this.technicianRepository = technicianRepository;
    }

    public VehicleUsageDTO registrar(VehicleUsageDTO dto) {

        Technician technician = technicianRepository.findById(dto.getTechnicianId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        boolean emUso = usoRepository.existsByVeiculoAndDataFimIsNull(dto.getVehicle());

        if (emUso) {
            throw new RuntimeException("Vehicle already in use");
        }

        VehicleUsage uso = new VehicleUsage();
        uso.setTechnician(technician);
        uso.setVehicle(dto.getVehicle());
        uso.setStartDate(dto.getStartDatetime());

        uso = usoRepository.save(uso);

        VehicleUsageDTO response = new VehicleUsageDTO();
        response.setTechnicianId(uso.getTechnician().getTechnicianId());
        response.setVehicle(uso.getVehicle());
        response.setStartDatetime(uso.getStartDate());

        return response;
    }

    public List<ActiveUsageDTO> listarEmUso() {

        List<VehicleUsage> ativos = usoRepository.findByDataFimIsNull();

        return ativos.stream()
                .map(u -> new ActiveUsageDTO(
                        u.getTechnician().getName(),
                        u.getVehicle()
                ))
                .collect(Collectors.toList());
    }
}