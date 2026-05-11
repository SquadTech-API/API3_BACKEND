package br.com.edu.fatec.IPEMControl.DTO;

import java.time.LocalDateTime;

public class UsageHistoryDTO {

    private String description;
    private LocalDateTime registrationDate;

    public UsageHistoryDTO() {
    }

    public UsageHistoryDTO(String description, LocalDateTime registrationDate) {
        this.description = description;
        this.registrationDate = registrationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}