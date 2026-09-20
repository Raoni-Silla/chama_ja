package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.StatusChamado;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 100)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusChamado statusChamado; // Ex: ABERTO, EM_ANDAMENTO, CONCLUIDO, CANCELADO

    @NotNull
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente; // Quem abriu o chamado

    @ManyToOne
    @JoinColumn(name = "prestador_id")
    private Prestador prestador; // Quem aceitou (pode ser null enquanto estiver ABERTO)

    private LocalDateTime dataCriacaoChamado;

    @Future(message = "O agendamento deve ser para uma data futura")
    private LocalDateTime praQuandoFoiAgendado;

    @ManyToOne
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoChamado> fotos;

    @ManyToMany
    @JoinTable(
            name = "chamado_categoria",
            joinColumns = @JoinColumn(name = "chamado_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias;

    @OneToMany(
            mappedBy = "chamado",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Pagamento> pagamentos = new ArrayList<>();

    // Campos para controle de conclusão (O "Double Check")
    private boolean concluidoPeloCliente;
    private boolean concluidoPeloPrestador;
    private LocalDateTime dataFinalizacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacaoChamado = LocalDateTime.now();
        this.statusChamado = StatusChamado.ABERTO;
        this.concluidoPeloCliente = false;
        this.concluidoPeloPrestador = false;
    }
}