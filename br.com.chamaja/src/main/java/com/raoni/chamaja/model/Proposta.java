package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.StatusProposta;
import com.raoni.chamaja.enums.Urgencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotNull
    private BigDecimal valorOrcado; // Preenchido pelo prestador ou sugerido pelo cliente

    @NotBlank
    @Size(max = 150)
    private String descricao; // "Preciso disso e disso"

    @Future
    private LocalDateTime dataHoraServico;

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