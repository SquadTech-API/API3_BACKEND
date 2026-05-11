package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BuscaAbastecimentoDTO {
    private List<FuelingItemDTO> abastecimentos;
    private List<ItemTrocaOleoDTO> trocasOleo;
    private List<ConsumoVeiculoDTO> veiculos;
}