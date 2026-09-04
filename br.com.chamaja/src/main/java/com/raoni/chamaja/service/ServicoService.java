package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Servico.ServicoResponseDTO;
import com.raoni.chamaja.enums.StatusChamado;
import com.raoni.chamaja.enums.StatusPagamento;
import com.raoni.chamaja.enums.StatusProposta;
import com.raoni.chamaja.enums.TipoTransacao;
import com.raoni.chamaja.model.*;
import com.raoni.chamaja.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ChamadoRepository chamadoRepository;
    private final PropostaRepository propostaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestadorRepository prestadorRepository;
    private final PagamentoRepository pagamentoRepository;
    private final TransacaoRepository transacaoRepository;


    private Long obterIdUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }

    private List<ServicoResponseDTO> listarServicosUsuarioLogado() {
        Usuario usuario = usuarioRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa usuario logado"));
        List<Chamado> chamados = chamadoRepository.findByClienteAndStatusChamadoIn(usuario, List.of(StatusChamado.EM_ANDAMENTO, StatusChamado.CONCLUIDO));
        return chamados.stream().map(c -> {
            Proposta proposta = propostaRepository.findByChamadoAndStatus(c, StatusProposta.ACEITA).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse proposta"));
            return new ServicoResponseDTO(
                    c.getId(),
                    c.getTitulo(),
                    c.getStatusChamado(),
                    c.getPrestador().getId(),
                    c.getPrestador().getNome(),
                    c.getPrestador().getFotoUrl(),
                    c.getDataCriacaoChamado(),
                    c.getPraQuandoFoiAgendado(),
                    c.getDataFinalizacao(),
                    proposta.getValorOrcado(),
                    c.isConcluidoPeloCliente(),
                    c.isConcluidoPeloPrestador()
            );
        }).toList();
    }


    private List<ServicoResponseDTO> listarServicosPrestadorLogado() {
        Prestador prestador = prestadorRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa usuario logado"));
        List<Chamado> chamados = chamadoRepository.findByPrestadorAndStatusChamadoIn(prestador, List.of(StatusChamado.EM_ANDAMENTO, StatusChamado.CONCLUIDO));
        return chamados.stream().map(c -> {
            Proposta proposta = propostaRepository.findByChamadoAndStatus(c, StatusProposta.ACEITA).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse proposta"));
            return new ServicoResponseDTO(
                    c.getId(),
                    c.getTitulo(),
                    c.getStatusChamado(),
                    c.getCliente().getId(),
                    c.getCliente().getNome(),
                    c.getCliente().getFotoUrl(),
                    c.getDataCriacaoChamado(),
                    c.getPraQuandoFoiAgendado(),
                    c.getDataFinalizacao(),
                    proposta.getValorOrcado(),
                    c.isConcluidoPeloCliente(),
                    c.isConcluidoPeloPrestador()
            );
        }).toList();
    }

    public List<ServicoResponseDTO> listarServicos() {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ROLE_USUARIO"))) {
            return listarServicosUsuarioLogado();
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(P -> P.getAuthority().equals("ROLE_PRESTADOR"))) {
            return listarServicosPrestadorLogado();
        } else {
            throw new EntityNotFoundException("Você não está logado");
        }
    }


    @Transactional
    public void confirmarConclusao(Long idChamado) {
        Chamado chamado = chamadoRepository.findById(idChamado).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse chamado"));
        if (!chamado.getStatusChamado().equals(StatusChamado.EM_ANDAMENTO)) {
            throw new IllegalArgumentException("Esse chamado não está mais válido para confirmação");
        }

        Long idLogado = obterIdUsuarioLogado();

        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ROLE_USUARIO"))) {
            if (!chamado.getCliente().getId().equals(idLogado)) {
                throw new IllegalArgumentException("Você não faz parte desse chamado");
            }
            chamado.setConcluidoPeloCliente(true);
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(P -> P.getAuthority().equals("ROLE_PRESTADOR"))) {
            if (!chamado.getPrestador().getId().equals(idLogado)) {
                throw new IllegalArgumentException("Você não faz parte desse chamado");
            }
            chamado.setConcluidoPeloPrestador(true);
        }


        if (chamado.isConcluidoPeloCliente() && chamado.isConcluidoPeloPrestador()) {

            Pagamento pagamento = pagamentoRepository.findByChamado(chamado).orElseThrow(() -> new EntityNotFoundException("Impossível encontrar o pagamento deste chamado"));
            if (!pagamento.getStatus().equals(StatusPagamento.RETIDO)) {
                throw new IllegalArgumentException("Pagamento deve ter status retido para liberação");
            }
            chamado.setStatusChamado(StatusChamado.CONCLUIDO);
            chamado.setDataFinalizacao(LocalDateTime.now());
            pagamento.setStatus(StatusPagamento.LIBERADO);
            pagamento.setDataLiberacao(LocalDateTime.now());

            chamado.getPrestador().getCarteira().adicionarValorSaldoDisponivel(pagamento.getValor());
            chamado.getPrestador().concluirServico();

            TransacaoCarteira transacaoCarteira = new TransacaoCarteira();
            transacaoCarteira.setTipoTransacao(TipoTransacao.ENTRADA_SERVICO);
            transacaoCarteira.setValorTransacao(pagamento.getValor());
            transacaoCarteira.setCarteira(chamado.getPrestador().getCarteira());
            transacaoCarteira.setDescricao(chamado.getDescricao());

            transacaoRepository.save(transacaoCarteira);
            pagamentoRepository.save(pagamento);
            chamadoRepository.save(chamado);
        }

    }


}
