package com.raoni.chamaja.dto.Prestador;

import java.util.List;

public record MelhoresDoMesDTO(
        Long id,
        String nome,
        String urlFoto,
        List<String> categorias,
        Double notaMedia,
        boolean isVerificado,
        Long quantidadeAvaliacoes,
        Double distanciaKm
) {
}
