package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.InteracaoInicial.InteracaoIniciaInfoUteisParaPrestador;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialRequestDTO;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialResponseDTO;
import com.raoni.chamaja.enums.StatusChamado;
import com.raoni.chamaja.enums.StatusInteracao;
import com.raoni.chamaja.model.Chamado;
import com.raoni.chamaja.model.InteracaoInicial;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteracaoInicialService {

    private final InteracaoInicialRepository interacaoRepo;
    private final UsuarioRepository userRepo;
    private final PrestadorRepository prestadorRepo;
    private final ChamadoRepository chamadoRepository;
    private final EnderecoRepository enderecoRepo;


    private Long obterIdUsuarioLogado() {
        var id = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(String.valueOf(id.getName()));
    }


    @Transactional
    public InteracaoInicialResponseDTO iniciarInteracaoUsuarioParaPrestador(InteracaoInicialRequestDTO dto) {
        Usuario remetente = userRepo.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        Prestador destinatario = prestadorRepo.findById(dto.idDestinatario()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        if (remetente.getId().equals(dto.idDestinatario())) {
            throw new IllegalArgumentException("Você não pode solicitar uma interação com você mesmo");
        }
        if (interacaoRepo.existsByRemetenteAndDestinatarioAndStatus(remetente, destinatario, StatusInteracao.PENDENTE) || chamadoRepository.existsByClienteAndPrestadorAndStatusChamado(remetente, destinatario, StatusChamado.EM_ANDAMENTO) || chamadoRepository.existsByClienteAndPrestadorAndStatusChamado(remetente, destinatario, StatusChamado.ABERTO)) {
            throw new IllegalStateException("você já tem uma negociação aberta com esse prestador, por favor aguarde até o fim da mesma para iniciar outra");
        }
        InteracaoInicial interacaoInicial = new InteracaoInicial();
        interacaoInicial.setTitulo(dto.titulo());
        interacaoInicial.setMensagem(dto.descricao());
        interacaoInicial.setValorSugerido(dto.valorSugerido());
        interacaoInicial.setRemetente(remetente);
        interacaoInicial.setDestinatario(destinatario);
        // data e status o @prePersist salva pra mim como pendente e now
        interacaoInicial = interacaoRepo.save(interacaoInicial);

        return new InteracaoInicialResponseDTO(
                interacaoInicial.getId(),
                interacaoInicial.getTitulo(),
                interacaoInicial.getMensagem(),
                interacaoInicial.getValorSugerido(),
                interacaoInicial.getDataCriacao(),
                interacaoInicial.getStatus()
        );
    }

    public List<InteracaoIniciaInfoUteisParaPrestador> obterListaDeInteracoesPendentes() {
        Prestador prestador = prestadorRepo.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse prestador"));
        List<InteracaoInicial> interacoesPendentes = interacaoRepo.findByDestinatarioAndStatus(prestador, StatusInteracao.PENDENTE);
        return interacoesPendentes.stream().map(i -> {
            Double distanciaKm = enderecoRepo.calcularDistanciaKm(i.getDestinatario().getId(), i.getRemetente().getId()).orElse(null);
            return new InteracaoIniciaInfoUteisParaPrestador(
                    i.getId(),
                    i.getTitulo(),
                    i.getMensagem(),
                    i.getValorSugerido(),
                    i.getRemetente().getId(),
                    i.getRemetente().getNome(),
                    i.getRemetente().getFotoUrl(),
                    i.getRemetente().getNotaMedia(),
                    distanciaKm
            );
        }).toList();
    }

    @Transactional
    public Long comecarNegociacao(Long idInteracao) {
        InteracaoInicial interacaoInicial = interacaoRepo.findById(idInteracao).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar essa interação"));
        Prestador prestador = prestadorRepo.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse prestador"));
        if (!interacaoInicial.getDestinatario().getId().equals(prestador.getId())){
            throw new IllegalArgumentException("Você está tentando violar essa interação");
        }
        if (!interacaoInicial.getStatus().equals(StatusInteracao.PENDENTE)) {
            throw new IllegalArgumentException("Impossivel continuar para negociação, interação já finalizada");
        }
        Chamado chamado = new Chamado();
        chamado.setTitulo(interacaoInicial.getTitulo());
        chamado.setDescricao(interacaoInicial.getMensagem());
        chamado.setCliente(interacaoInicial.getRemetente());
        chamado.setPrestador(prestador);

        chamado = chamadoRepository.save(chamado);
        interacaoInicial.setStatus(StatusInteracao.ACEITA);
        interacaoInicial.setChamado(chamado);

        return chamado.getId();
    }

}
