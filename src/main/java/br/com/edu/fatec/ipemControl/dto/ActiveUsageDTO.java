package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveUsageDTO {

    private String technicianName;
    private String vehicle;
}