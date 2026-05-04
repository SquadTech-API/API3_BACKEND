package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_servico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_servico")
    private Integer idTipoServico;

    @Column(name = "nome_servico", nullable = false, length = 100)
    private String nomeServico;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    // CORRIGIDO: campos ausentes que existem no banco de dados
    @Column(name = "habilitado", nullable = false)
    private Boolean habilitado = true;

    @Column(name = "eh_troca_oleo", nullable = false)
    private Boolean ehTrocaOleo = false;
}