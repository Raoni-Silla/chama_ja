package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Pagamento.PagamentoRequestDTO;
import com.raoni.chamaja.enums.StatusPagamento;
import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.Pagamento;
import com.raoni.chamaja.repository.ChamadoRepository;
import com.raoni.chamaja.repository.PagamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ChamadoRepository chamadoRepository;

    private Long obterIdUsuarioLogado(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }

    @Transactional
    public void pagar(PagamentoRequestDTO pagamentoRequestDTO) {
        Chamado chamado = chamadoRepository.findById(pagamentoRequestDTO.idChamado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse chamado"));
        Pagamento pagamento = pagamentoRepository.findByChamado(chamado).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse pagamento para esse chamado"));
        if (!obterIdUsuarioLogado().equals(chamado.getCliente().getId())) {
            throw new IllegalArgumentException("Impossivel pagar esse chamado, você não pertence ao mesmo");
        }
        if (!pagamento.getStatus().equals(StatusPagamento.PENDENTE)){
            throw new IllegalArgumentException("Pagamento não está pendente");
        }
        pagamento.setMetodoPagamento(pagamentoRequestDTO.metodoPagamento());
        pagamento.setStatus(StatusPagamento.RETIDO);
        pagamento.getChamado().getPrestador().getCarteira().adicionarValorNoSaldoBloqueado(pagamento.getValor());
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamentoRepository.save(pagamento);
    }

}
