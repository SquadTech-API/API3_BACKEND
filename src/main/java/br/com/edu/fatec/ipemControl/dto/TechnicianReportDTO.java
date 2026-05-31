package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class TechnicianReportDTO {

    // Seção 1 — Identificação
    private Integer registration;
    private String name;
    private String role;
    private String userType;
    private String licenseType;
    private String licenseNumber;
    private String cpf;
    private String email;
    private String birthDate;

    // Seção 2 — Status operacional
    private Boolean active;
    private String registrationDate;
    private String lastUpdate;
    private Boolean openDeparture;
    private Integer openDepartureId;

    // Seção 3 — Uso por período
    private Map<String, Long> departuresByPeriod;
    private Map<String, BigDecimal> kmByPeriod;
    private String lastDepartureDate;
    private String lastDepartureVehicle;
    private String lastDepartureDestination;

    // Seção 4 — Comportamento operacional
    private Double avgDurationHours;
    private BigDecimal longestKm;
    private Double longestDurationHours;
    private Double departureFrequencyPerWeek;

    // Seção 5 — Financeiro
    private Map<String, BigDecimal> spendingByPeriod;
    private Map<String, Long> fuelingsByPeriod;

    // Seção 6 — Manutenção
    private Long oilChanges;
    private String lastOilChange;
    private List<String> usedVehicles;

    // Seção 7 — Documentos
    private DocumentsDTO documents;

    // Destinos e serviços
    private List<FrequentDestinationDTO> destinations;
    private Map<String, Long> services;

    @Data
    public static class DocumentsDTO {
        private Long received;
        private Long read;
        private Long downloaded;
    }
}