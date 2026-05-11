package br.com.edu.fatec.IPEMControl.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsageHistoryCardDTO {
    private String driver;       // Nome do motorista aqui
    private LocalDateTime departureDate;
    private String serviceType;
    private BigDecimal traveledKm;
    private boolean fueled;      // Destacado em verde se for true
}