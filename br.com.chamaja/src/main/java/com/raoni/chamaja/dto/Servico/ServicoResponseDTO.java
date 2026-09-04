package com.raoni.chamaja.dto.Servico;

import com.raoni.chamaja.enums.StatusChamado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicoResponseDTO(
        Long idChamado,
        String titulo,
        StatusChamado statusChamado,
        Long idOutraPessoa,
        String nomeOutraPessoa,
        String fotoOutraPessoa,
        LocalDateTime dataCriacao,
        LocalDateTime dataHoraServico,
        LocalDateTime horaFinalizacao,
        BigDecimal valorServico,
        boolean concluidoPeloCliente,
        boolean concluidoPeloPrestador
) {
}
