package com.raoni.chamaja.dto.InteracaoInicial;

import com.raoni.chamaja.enums.StatusInteracao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record   InteracaoInicialResponseDTO(
        Long id,
        String titulo,
        String descricao,
        BigDecimal valorSugerido,
        LocalDateTime dataCriacao,
        StatusInteracao statusInteracao
) {
}
