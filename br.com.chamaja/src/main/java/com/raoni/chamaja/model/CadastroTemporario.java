package com.raoni.chamaja.model;

import com.raoni.chamaja.enums.StatusCadastro;
import com.raoni.chamaja.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class CadastroTemporario {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Size(min = 3 , max = 100)
    private String nome;

    @NotBlank
    private String cpf;

    private LocalDate dataNascimento;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String senha;

    private String telefone;

    private Boolean telefoneValidado;

    private String codigoSms;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    @Enumerated(EnumType.STRING)
    private StatusCadastro status;

    @OneToOne (cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_temp_id")
    private EnderecoTemporario enderecoTemporario;
}