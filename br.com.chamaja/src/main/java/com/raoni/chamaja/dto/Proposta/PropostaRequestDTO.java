package com.raoni.chamaja.dto.Proposta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropostaRequestDTO(
        @NotNull
        @DecimalMin("0.01")
        BigDecimal valorOrcado,
        @NotBlank
        String descricao,
        @NotNull
        @Future
        LocalDateTime dataHoraServico
) {
}
