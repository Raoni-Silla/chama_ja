package com.raoni.chamaja.dto.Proposta;

import com.raoni.chamaja.enums.StatusProposta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PropostaResponseDTO (
        Long id,
        Long idChamado,
        BigDecimal valorOrcado,
        String descricao,
        LocalDateTime dataHoraServico,
        StatusProposta status,
        LocalDateTime dataCriacao
) {
}
