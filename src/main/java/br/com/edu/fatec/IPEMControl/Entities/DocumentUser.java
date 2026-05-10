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
public class DocumentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_doc")
    private Integer userDocId;

    @Column(name = "data_acesso")
    private LocalDateTime accessDate;

    @Column(name = "baixado")
    private Boolean isDownloaded = false;

    @Column(name = "lido")
    private Boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "matricula")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_documento")
    private Document document;
}