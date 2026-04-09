package br.com.edu.fatec.IPEMControl.DTO;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import java.math.BigDecimal;
import java.util.List;


public class RelatorioUsoMensalDTO {

    private BigDecimal quilometragemTotal;
    private Integer totalDeViagens;
    private List<RegistroSaida> detalhes;

    public RelatorioUsoMensalDTO() {}

    public RelatorioUsoMensalDTO(BigDecimal quilometragemTotal, Integer totalDeViagens, List<RegistroSaida> detalhes) {
        this.quilometragemTotal = quilometragemTotal;
        this.totalDeViagens = totalDeViagens;
        this.detalhes = detalhes;
    }

    // Getters e Setters
    public BigDecimal getQuilometragemTotal() { return quilometragemTotal; }
    public void setQuilometragemTotal(BigDecimal quilometragemTotal) { this.quilometragemTotal = quilometragemTotal; }

    public Integer getTotalDeViagens() { return totalDeViagens; }
    public void setTotalDeViagens(Integer totalDeViagens) { this.totalDeViagens = totalDeViagens; }

    public List<RegistroSaida> getDetalhes() { return detalhes; }
    public void setDetalhes(List<RegistroSaida> detalhes) { this.detalhes = detalhes; }
}