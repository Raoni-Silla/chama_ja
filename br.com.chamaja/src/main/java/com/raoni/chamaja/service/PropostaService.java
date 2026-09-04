package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Proposta.PropostaRequestDTO;
import com.raoni.chamaja.dto.Proposta.PropostaResponseDTO;
import com.raoni.chamaja.enums.StatusChamado;
import com.raoni.chamaja.enums.StatusProposta;
import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.Pagamento;
import com.raoni.chamaja.model.Proposta;
import com.raoni.chamaja.repository.ChamadoRepository;
import com.raoni.chamaja.repository.PagamentoRepository;
import com.raoni.chamaja.repository.PropostaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PropostaService {

    private final ChamadoRepository chamadoRepository;
    private final PropostaRepository propostaRepository;
    private final PagamentoRepository pagamentoRepository;

    private Long obterIdUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    public PropostaResponseDTO criarProposta(Long idChamado, PropostaRequestDTO dto) {

        Chamado chamado = chamadoRepository.findById(idChamado).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse chamado"));
        Long id = obterIdUsuarioLogado();
        if (!Objects.equals(chamado.getPrestador().getId(), id)) {
            throw new IllegalArgumentException("Impossivel fazer qualquer alteração, pois voce nao participa dessa proposta");
        }
        if (chamado.getStatusChamado() != StatusChamado.ABERTO) {
            throw new RuntimeException("Impossivel enviar uma nova proposta");
        }

        if (propostaRepository.existsByChamadoAndStatus(chamado, StatusProposta.PENDENTE)) {
            throw new IllegalArgumentException("Já tem uma proposta pendente nesse chamado");
        }

        Proposta proposta = new Proposta();
        proposta.setChamado(chamado);
        proposta.setDescricao(dto.descricao());
        proposta.setValorOrcado(dto.valorOrcado());
        proposta.setDataHoraServico(dto.dataHoraServico());

        proposta = propostaRepository.save(proposta);

        return createPropostaResponseDTO(proposta);
    }


    public PropostaResponseDTO obterPropostaPendente (Long idChamado){
        Chamado chamado = chamadoRepository.findById(idChamado).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse chamado"));
        Long id = obterIdUsuarioLogado();
        if (!Objects.equals(chamado.getPrestador().getId(), id) && !Objects.equals(chamado.getCliente().getId(), id)) {
            throw new IllegalArgumentException("Impossivel fazer qualquer alteração, pois voce nao participa dessa proposta");
        }
        Proposta proposta = propostaRepository.findByChamadoAndStatus(chamado,StatusProposta.PENDENTE).orElseThrow(() -> new EntityNotFoundException("Nã há propostas pendentes para esse chamado"));
        return createPropostaResponseDTO(proposta);
    }

    private static @NonNull PropostaResponseDTO createPropostaResponseDTO(Proposta proposta) {
        return new PropostaResponseDTO(
                proposta.getId(),
                proposta.getChamado().getId(),
                proposta.getValorOrcado(),
                proposta.getDescricao(),
                proposta.getDataHoraServico(),
                proposta.getStatus(),
                proposta.getDataCriacao()
        );
    }


    public void recusarProposta (Long idProposta){
        Proposta proposta = propostaRepository.findById(idProposta).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa proposta"));
        Long id = obterIdUsuarioLogado();
        if (!Objects.equals(proposta.getChamado().getCliente().getId(), id)){
            throw new IllegalArgumentException("essa proposta não foi direcionada para você");
        }
        if (proposta.getStatus() != StatusProposta.PENDENTE ){
            throw new IllegalArgumentException("Essa proposta já foi aceita ou indeferida");
        }
        proposta.setStatus(StatusProposta.RECUSADA);
        propostaRepository.save(proposta);
    }

    @Transactional
    public void aceitarProposta(Long idProposta) {
        Proposta proposta = propostaRepository.findById(idProposta).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa proposta"));
        Long id = obterIdUsuarioLogado();
        if (!Objects.equals(proposta.getChamado().getCliente().getId(), id)){
            throw new IllegalArgumentException("essa proposta não foi direcionada para você");
        }
        if (proposta.getStatus() != StatusProposta.PENDENTE ){
            throw new IllegalArgumentException("Essa proposta já foi aceita ou indeferida");
        }
        if (proposta.getChamado().getStatusChamado() == StatusChamado.ABERTO){
            proposta.getChamado().setStatusChamado(StatusChamado.EM_ANDAMENTO);
        }else {
            throw new IllegalArgumentException("Esse chamado não está mais aberto para aceitar propostas");
        }
        proposta.setStatus(StatusProposta.ACEITA);

        proposta.getChamado().setPraQuandoFoiAgendado(proposta.getDataHoraServico());

        Pagamento pagamento = new Pagamento();
        pagamento.setValor(proposta.getValorOrcado());
        pagamento.setUsuario(proposta.getChamado().getCliente());
        pagamento.setChamado(proposta.getChamado());
        pagamento.setMetodoPagamento(null);
        pagamento.setDataPagamento(null);
        pagamento.setDataLiberacao(null);

        pagamentoRepository.save(pagamento);

        propostaRepository.save(proposta);
    }
}
