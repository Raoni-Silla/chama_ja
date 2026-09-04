package com.raoni.chamaja.dto.Pagamento;

import com.raoni.chamaja.enums.MetodoPagamento;
import jakarta.validation.constraints.NotNull;

public record PagamentoRequestDTO(
        @NotNull
        Long idChamado,
        MetodoPagamento metodoPagamento
) {
}
