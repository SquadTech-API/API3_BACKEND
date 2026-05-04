package br.com.edu.fatec.IPEMControl.DTO;

import br.com.edu.fatec.IPEMControl.Entities.RegistroSaida;
import java.math.BigDecimal;
import java.util.List;

public class RelatorioUsoMensalDTO {

    private BigDecimal quilometragemTotal;
    private Integer totalDeViagens;
    private BigDecimal gastoTotal;   // Adicionado para o R$ do Dashboard
    private BigDecimal litrosTotal;  // Adicionado para o L do Dashboard
    private List<RegistroSaida> detalhes;

    public RelatorioUsoMensalDTO() {}

    // Construtor atualizado
    public RelatorioUsoMensalDTO(BigDecimal quilometragemTotal, Integer totalDeViagens, BigDecimal gastoTotal, BigDecimal litrosTotal, List<RegistroSaida> detalhes) {
        this.quilometragemTotal = quilometragemTotal;
        this.totalDeViagens = totalDeViagens;
        this.gastoTotal = gastoTotal;
        this.litrosTotal = litrosTotal;
        this.detalhes = detalhes;
    }

    // Getters e Setters
    public BigDecimal getQuilometragemTotal() { return quilometragemTotal; }
    public void setQuilometragemTotal(BigDecimal quilometragemTotal) { this.quilometragemTotal = quilometragemTotal; }

    public Integer getTotalDeViagens() { return totalDeViagens; }
    public void setTotalDeViagens(Integer totalDeViagens) { this.totalDeViagens = totalDeViagens; }

    public BigDecimal getGastoTotal() { return gastoTotal; }
    public void setGastoTotal(BigDecimal gastoTotal) { this.gastoTotal = gastoTotal; }

    public BigDecimal getLitrosTotal() { return litrosTotal; }
    public void setLitrosTotal(BigDecimal litrosTotal) { this.litrosTotal = litrosTotal; }

    public List<RegistroSaida> getDetalhes() { return detalhes; }
    public void setDetalhes(List<RegistroSaida> detalhes) { this.detalhes = detalhes; }
}