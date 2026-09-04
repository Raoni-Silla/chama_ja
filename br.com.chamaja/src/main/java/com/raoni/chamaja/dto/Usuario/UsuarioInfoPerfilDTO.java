package com.raoni.chamaja.dto.Usuario;

public record UsuarioInfoPerfilDTO(
        String nome,
        String email,
        String telefone,
        String urlFoto,
        String cpf,
        boolean verificado,
        Long raioBusca

) {
}
