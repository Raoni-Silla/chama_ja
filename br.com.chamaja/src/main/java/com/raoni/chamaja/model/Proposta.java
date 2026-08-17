package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.StatusProposta;
import com.raoni.chamaja.enums.Urgencia;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor; // Quem criou a proposta (pode ser o cliente ou o prestador)

    private BigDecimal valorOrcado; // Preenchido pelo prestador ou sugerido pelo cliente

    private String descricao; // "Preciso disso e disso"

    @Enumerated(EnumType.STRING)
    private Urgencia urgencia;

    @Enumerated(EnumType.STRING)
    private StatusProposta status; // PENDENTE, ACEITA, RECUSADA, CONCLUIDA

    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusProposta.PENDENTE;
    }
}