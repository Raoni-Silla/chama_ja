package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.StatusInteracao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InteracaoInicial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remetente_id")
    private Usuario remetente;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    // Se a interação nasceu de um chamado específico
    @ManyToOne
    @JoinColumn(name = "chamado_id")
    private Chamado chamado;

    private String titulo; // "Preciso de conserto de pia"

    @Column(columnDefinition = "TEXT")
    private String mensagem; // Descrição formatada

    private BigDecimal valorSugerido; // Caso o prestador esteja enviando

    @Enumerated(EnumType.STRING)
    private StatusInteracao status; // PENDENTE, ACEITA, RECUSADA

    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusInteracao.PENDENTE;
    }
}