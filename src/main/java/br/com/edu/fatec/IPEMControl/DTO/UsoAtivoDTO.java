package br.com.edu.fatec.IPEMControl.DTO;

public class UsoAtivoDTO {

    private String nomeTecnico;
    private String veiculo;

    public UsoAtivoDTO(String nomeTecnico, String veiculo) {
        this.nomeTecnico = nomeTecnico;
        this.veiculo = veiculo;
    }

    public String getNomeTecnico() { return nomeTecnico; }
    public String getVeiculo() { return veiculo; }
}