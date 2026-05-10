package br.com.edu.fatec.IPEMControl.DTO;

public class DashboardComparativoDTO {

    private String veiculo;
    private Long totalUsos;
    private Double horasUso;

    public DashboardComparativoDTO(String veiculo, Long totalUsos, Double horasUso) {
        this.veiculo = veiculo;
        this.totalUsos = totalUsos;
        this.horasUso = horasUso;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public Long getTotalUsos() {
        return totalUsos;
    }

    public Double getHorasUso() {
        return horasUso;
    }
}