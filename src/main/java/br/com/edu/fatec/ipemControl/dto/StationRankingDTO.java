package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationRankingDTO {
    private String stationName;
    private String stationCity;
    private Long visitCount;
}