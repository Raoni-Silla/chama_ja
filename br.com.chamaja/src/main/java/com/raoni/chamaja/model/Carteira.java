package com.raoni.chamaja.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // BigDecimal.ZERO garante que não comece nulo
    @Column(precision = 19, scale = 2)
    private BigDecimal saldoDisponivel = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal saldoBloqueado = BigDecimal.ZERO;

    private String chavePix;

    @OneToOne(mappedBy = "carteira")
    private Prestador prestador;

    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL)
    private List<TransacaoCarteira> transacoes;

    @PrePersist
    public void prePersist() {
        if (this.saldoDisponivel == null) this.saldoDisponivel = BigDecimal.ZERO;
        if (this.saldoBloqueado == null) this.saldoBloqueado = BigDecimal.ZERO;
    }

    public void adicionarValorNoSaldoBloqueado (BigDecimal valor){
        this.saldoBloqueado = this.saldoBloqueado.add(valor);
    }

    public void adicionarValorSaldoDisponivel (BigDecimal valor){
        if (valor.compareTo(this.saldoBloqueado) > 0){
            throw  new RuntimeException("Saldo negativo");
        }
        saldoBloqueado = saldoBloqueado.subtract(valor);
        this.saldoDisponivel = this.saldoDisponivel.add(valor);
    }
}