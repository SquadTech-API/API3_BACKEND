package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoResumoDTO {

    private Integer idVeiculo;
    private String modelo;
    private String prefixo;
    private String ultimoUso;
    private String ultimoMotorista;
    private String ultimoAbastecimento;
    private String km;
    private String status;
    private String habilitacaoCategoria;
    private Boolean ativo;

    // Construtor de compatibilidade sem os novos campos (para não quebrar código existente)
    public VeiculoResumoDTO(Integer idVeiculo, String modelo, String prefixo,
                            String ultimoUso, String ultimoMotorista,
                            String ultimoAbastecimento, String km, String status) {
        this.idVeiculo           = idVeiculo;
        this.modelo              = modelo;
        this.prefixo             = prefixo;
        this.ultimoUso           = ultimoUso;
        this.ultimoMotorista     = ultimoMotorista;
        this.ultimoAbastecimento = ultimoAbastecimento;
        this.km                  = km;
        this.status              = status;
        this.ativo               = true;
    }
}