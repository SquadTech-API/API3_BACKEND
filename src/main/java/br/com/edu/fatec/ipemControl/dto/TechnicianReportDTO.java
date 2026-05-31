package br.com.edu.fatec.ipemControl.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Relatório individual completo de um técnico.
 * Resposta do GET /relatorios/technicians/{registration}?periodo={periodo}
 *
 * Removidos por serem responsabilidade do frontend:
 *   - alertas         (derivado dos data já presentes no response)
 *   - eficiencia      (totalKm / totalSpending)
 *   - mediaMensal     (saidas / 4)
 *   - taxaLeitura     ((read / received) * 100)
 */
@Data
public class TechnicianReportDTO {

    // ── Seção 1: Identificação ────────────────────────────────────────────────
    private Integer registration;
    private String name;
    private String role;
    private String type;
    private String driversLicense;
    private String licenseNumber;
    private String cpf;
    private String email;
    private String phone;
    /** ISO 8601: YYYY-MM-DD */
    private String birthDate;

    // ── Seção 2: Status operacional ──────────────────────────────────────────
    private Boolean active;
    /** ISO 8601: YYYY-MM-DDTHH:mm:ss */
    private String registrationDate;
    /** ISO 8601: YYYY-MM-DDTHH:mm:ss */
    private String lastUpdate;
    private Boolean openDeparture;
    private Integer openDepartureId;

    // ── Seção 3: Uso do sistema ───────────────────────────────────────────────
    /** Map<"hoje"|"7"|"30"|"year", total de saídas> */
    private Map<String, Long> departuresByPeriod;
    /** Map<"hoje"|"7"|"30"|"year", mileage total> */
    private Map<String, BigDecimal> kmByPeriod;
    private String lastDepartureDate;
    private String lastDepartureVehicle;
    private String lastDepartureDestination;

    // ── Seção 4: Comportamento operacional ───────────────────────────────────
    private Double avgDepartureDurationHours;
    private BigDecimal longestDepartureKm;
    private Double longestDepartureDurationHours;
    private Double departureFrequencyPerWeek;

    // ── Seção 5: Responsabilidade financeira ─────────────────────────────────
    /** Map<"hoje"|"7"|"30"|"year", gasto total> */
    private Map<String, BigDecimal> spendingByPeriod;
    /** Map<"hoje"|"7"|"30"|"year", total de refuels> */
    private Map<String, Long> refuelsByPeriod;

    // ── Seção 6: Manutenção ──────────────────────────────────────────────────
    private Long oilChanges;
    private String lastOilChange;
    private List<String> usedVehicles;

    // ── Seção 7: Documentos e compliance ─────────────────────────────────────
    private DocumentosDTO documents;

    // ── Destinos e serviços ───────────────────────────────────────────────────
    private List<FrequentDestinationDTO> destinations;
    /** Map<serviceName, count> */
    private Map<String, Long> services;

    // ─────────────────────────────────────────────────────────────────────────

    @Data
    public static class DocumentosDTO {
        private Long received;
        private Long read;
        private Long downloaded;
        // taxaLeitura removida — frontend calcula: (read / received) * 100
    }
}