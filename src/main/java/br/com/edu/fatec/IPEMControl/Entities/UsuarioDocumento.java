package br.com.edu.fatec.IPEMControl.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_documento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_doc")
    private Integer idUsuarioDoc;

    @Column(name = "data_acesso")
    private LocalDateTime dataAcesso;

    @Column(name = "baixado")
    private Boolean baixado = false;

    @Column(name = "lido")
    private Boolean lido = false;

    @ManyToOne
    @JoinColumn(name = "matricula")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_documento")
    private Documento documento;
}