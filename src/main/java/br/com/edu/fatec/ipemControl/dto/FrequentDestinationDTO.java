package br.com.edu.fatec.ipemControl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FrequentDestinationDTO {

    private String location;
    private Long quantity;
}