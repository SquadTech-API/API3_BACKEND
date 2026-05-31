package br.com.edu.fatec.ipemControl.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "technicians")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long technicianId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "driversLicense", nullable = false, unique = true)
    private String driveLicense;

    @Column(name = "phone")
    private String phone;
}