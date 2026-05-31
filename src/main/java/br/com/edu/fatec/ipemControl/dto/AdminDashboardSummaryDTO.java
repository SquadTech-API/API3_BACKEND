package br.com.edu.fatec.ipemControl.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdminDashboardSummaryDTO {

    private long totalVehicles;
    private long availableVehicles;
    private long vehiclesInUse;
    private long openDepartures;
    private long oilChangeAlerts;
    private long activeTechnicians;
    private long notTranscribedToSgi;
}