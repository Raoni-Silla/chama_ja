package com.raoni.chamaja.dto.Transacoes;

import com.raoni.chamaja.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UltimasTransacoesDTO(
        BigDecimal valor,
        TipoTransacao tipoTransacao,
        LocalDateTime data,
        String descricao
) {
}
