package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserRankingDTO {
    private String name;
    private Integer fuelSupplyCount;
    private BigDecimal totalSpent;
}