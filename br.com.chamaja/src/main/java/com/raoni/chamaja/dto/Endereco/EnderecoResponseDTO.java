package com.raoni.chamaja.dto.Endereco;

public record EnderecoResponseDTO(
        Long id,
        String logradouro,
        Long numero,
        String complemento,
        String nomeCidade,
        String siglaEstado,
        String cep,
        Double latitude, // e isso tambem
        Double longitude, //tirar isso aqui
        boolean enderecoPrincipal
) {
}
