package com.raoni.chamaja.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EnderecoTemporario {

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

}
