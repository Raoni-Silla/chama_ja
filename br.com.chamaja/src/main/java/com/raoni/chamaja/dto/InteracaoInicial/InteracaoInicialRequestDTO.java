package com.raoni.chamaja.dto.InteracaoInicial;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record InteracaoInicialRequestDTO(
        @NotBlank
        String titulo,
        @NotBlank
        String descricao,
        @NotNull
        @PositiveOrZero
        @Min(1)
        BigDecimal valorSugerido,
        @NotNull
        Long idDestinatario
) {
}
