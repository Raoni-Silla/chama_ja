package com.raoni.chamaja.dto.Servico;

import com.raoni.chamaja.dto.Endereco.EnderecoResponseDTO;
import com.raoni.chamaja.enums.StatusChamado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServicoSimplificadoDTO (
        Long idChamado,
        String titulo,
        StatusChamado statusChamado,
        Long idOutraPessoa,
        String nomeOutraPessoa,
        String fotoOutraPessoa,
        LocalDateTime dataHoraServico,
        BigDecimal valorServico,
        EnderecoResponseDTO endereco
){
}
