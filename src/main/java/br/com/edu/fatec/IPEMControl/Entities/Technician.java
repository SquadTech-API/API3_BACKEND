package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tecnicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long technicianId;

    @Column(name = "nome", nullable = false)
    private String name;

    @Column(name = "cnh", nullable = false, unique = true)
    private String driveLicense;

    @Column(name = "telefone")
    private String phone;
}