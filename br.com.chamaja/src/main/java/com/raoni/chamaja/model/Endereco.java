                                                                                                                                                                                                                                                                                                                                                                                                                                    package com.raoni.chamaja.model;

import com.raoni.chamaja.model.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

                                                                                                                                                                                                                                                                                                                                                                                                                                    @Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String logradouro;

    @NotNull
    private Long numero;

    @NotBlank
    private String nomeCidade;

    @NotBlank
    @Size(max = 2)
    private String siglaEstado;

    @NotBlank
    @Pattern(
            regexp = "^\\d{5}-?\\d{3}$",
            message = "CEP inválido"
    )
    private String cep;

    private String complemento;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @OneToMany(mappedBy = "endereco")
    private List<Chamado> chamados;

    private boolean enderecoPrincipal;


    @ManyToOne (optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}