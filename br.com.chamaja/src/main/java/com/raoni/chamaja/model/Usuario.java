package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @URL
    private String fotoUrl;

    @NotBlank
    @Size(min = 3 , max = 100)
    private String nome;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    private String telefone;

    private String telefonePendente;

    private LocalDateTime dataExpiracao;

    @NotBlank
    @Column(unique = true)
    private String cpf;

    private Double notaMedia;

    @PositiveOrZero(message = "O raio de atuação não pode ser negativo")
    private Long raioDeBusca;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    private LocalDateTime dataCriacao;

    private LocalDate dataDeNascimento;

    private boolean verificado;

    private String codigoSms;

    private boolean isContaAtiva;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Pagamento> pagamentos;

    @OneToMany(mappedBy = "cliente")
    private List<Proposta> propostas;

    @OneToMany(mappedBy = "cliente")
    private List<Chamado> chamados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartaoSalvo> cartoesSalvos;

    @Column(name = "mercado_pago_customer_id")
    private String mercadoPagoCustomerId;



    @PrePersist
    public void prePersist() {
        this.isContaAtiva = true;
        this.dataCriacao = LocalDateTime.now();
        this.verificado = false;
        if (this.fotoUrl == null || this.fotoUrl.isEmpty()) {
            this.fotoUrl = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + this.nome;
        }
    }
}

