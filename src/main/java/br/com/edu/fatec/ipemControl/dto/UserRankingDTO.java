package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UserRankingDTO {
    private String name;
    private Integer refuelCount;
    private BigDecimal totalSpent;
}