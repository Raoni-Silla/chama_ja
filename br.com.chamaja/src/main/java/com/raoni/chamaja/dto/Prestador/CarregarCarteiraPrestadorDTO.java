package com.raoni.chamaja.dto.Prestador;

import com.raoni.chamaja.dto.Pagamento.UltimosPagamentosDTO;
import com.raoni.chamaja.dto.Transacoes.UltimasTransacoesDTO;

import java.math.BigDecimal;
import java.util.List;

public record CarregarCarteiraPrestadorDTO(
        BigDecimal saldoDisponivel,
        String chavePix,
        List<UltimosPagamentosDTO> ultimosPagamentos,
        List<UltimasTransacoesDTO> ultimasTransacoes
) {
}
