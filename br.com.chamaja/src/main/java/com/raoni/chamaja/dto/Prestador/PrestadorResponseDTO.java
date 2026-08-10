package com.raoni.chamaja.dto.Prestador;

import java.math.BigDecimal;
import java.util.List;

public record PrestadorResponseDTO(
        Long id,
        String nome,
        String fotoUrl,
        String biografia,
        Double notaMedia,
        BigDecimal valorHora,
        String cidadePrincipal,
        List<String> categorias,
        Double distanciaKm
) {
}
