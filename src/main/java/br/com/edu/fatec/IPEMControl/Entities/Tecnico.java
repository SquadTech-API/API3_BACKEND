package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "técnicos") // Nome exato com acento como está no seu MySQL
public class Tecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer matricula; // Adicionado para bater com a busca do relatório

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cnh;

    private String telefone;

    public Tecnico() {}

    public Tecnico(Integer matricula, String nome, String cnh, String telefone) {
        this.matricula = matricula;
        this.nome = nome;
        this.cnh = cnh;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Long getId() { return id; }

    public Integer getMatricula() { return matricula; }
    public void setMatricula(Integer matricula) { this.matricula = matricula; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}