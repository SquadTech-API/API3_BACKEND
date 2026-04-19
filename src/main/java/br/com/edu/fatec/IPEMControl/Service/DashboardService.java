package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.DashboardComparativoDTO;
import br.com.edu.fatec.IPEMControl.Repository.UsoVeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private UsoVeiculoRepository usoVeiculoRepository;

    public List<DashboardComparativoDTO> buscarComparativo() {
        List<Object[]> resultados = usoVeiculoRepository.buscarComparativoUso();

        List<DashboardComparativoDTO> lista = new ArrayList<>();

        for (Object[] row : resultados) {
            String veiculo = (String) row[0];
            Long totalUsos = ((Number) row[1]).longValue();
            Double horasUso = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

            lista.add(new DashboardComparativoDTO(veiculo, totalUsos, horasUso));
        }

        return lista;
    }
}