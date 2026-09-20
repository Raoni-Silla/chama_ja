package com.raoni.chamaja.dto.Prestador;

import com.raoni.chamaja.dto.InteracaoInicial.InteracaoIniciaInfoUteisParaPrestador;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialResponseDTO;
import com.raoni.chamaja.dto.Servico.ServicoResponseDTO;
import com.raoni.chamaja.dto.Servico.ServicoSimplificadoDTO;

import java.util.List;

public record CarregarHomePrestadorDTO(
        String nome,
        String cidade,
        ServicoSimplificadoDTO proximoServico,
        List<InteracaoIniciaInfoUteisParaPrestador> solicitacoesPendentes
) {
}
