package com.raoni.chamaja.dto.InteracaoInicial;

import java.math.BigDecimal;

public record InteracaoIniciaInfoUteisParaPrestador(
        Long idInteracao,
        String titulo,
        String descricao,
        BigDecimal valorSugerido,
        Long idRemetente,
        String nomeRemetente,
        String fotoRemetente,
        Double avaliacaoRemetente,
        Double distanciaKmRemetente
) {
}
