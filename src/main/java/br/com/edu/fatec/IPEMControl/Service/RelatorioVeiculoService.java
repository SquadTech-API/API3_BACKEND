package br.com.edu.fatec.IPEMControl.Service;

import br.com.edu.fatec.IPEMControl.DTO.RelatorioVeiculoDTO;
import org.springframework.stereotype.Service;

@Service
public class RelatorioVeiculoService {

    public RelatorioVeiculoDTO gerarRelatorioVeiculo(Integer idVeiculo) {

        RelatorioVeiculoDTO dto = new RelatorioVeiculoDTO();

        dto.setPrefixo("V001");
        dto.setPlaca("ABC-1234");
        dto.setMarca("Fiat");
        dto.setModelo("Uno");
        dto.setAno(2022);
        dto.setCombustivel("Gasolina");
        dto.setKmRodado(15000.0);
        dto.setConsumoMedio(12.5);
        dto.setTotalSaidas(40);

        return dto;
    }
}