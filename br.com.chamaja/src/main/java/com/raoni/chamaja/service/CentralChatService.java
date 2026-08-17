package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Central.CentralGenericDTO;
import com.raoni.chamaja.dto.Chat.MensagemChatConteudoDTO;
import com.raoni.chamaja.dto.Chat.MensagemChatDTO;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialResponseDTO;
import com.raoni.chamaja.enums.EstadoCentral;
import com.raoni.chamaja.enums.StatusChamado;
import com.raoni.chamaja.enums.StatusInteracao;
import com.raoni.chamaja.enums.TipoItemCentral;
import com.raoni.chamaja.model.*;
import com.raoni.chamaja.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CentralChatService {

    private final ChamadoRepository chamadoRepository;
    private final InteracaoInicialRepository interacaoInicialRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensagemChatRepository mensagemChatRepository;
    private final PrestadorRepository prestadorRepository;

    private Long obterIdUsuarioLogado () {
        var autenthication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(String.valueOf(autenthication.getName()));
    }


    public List<CentralGenericDTO> listarContatosUsuarioLogado () {
        Usuario usuario = usuarioRepository.findById(obterIdUsuarioLogado())
                .orElseThrow(() -> new EntityNotFoundException("Impossível encontrar esse usuário"));

        List<InteracaoInicial> listaInteracoesUsuario = interacaoInicialRepository
                .findByRemetenteAndStatus(usuario, StatusInteracao.PENDENTE);

        List<Chamado> listaChamadosDoUsuario = chamadoRepository
                .findByClienteAndStatusChamadoIn(usuario, List.of(StatusChamado.ABERTO, StatusChamado.EM_ANDAMENTO));

        List<CentralGenericDTO> interacoesDto = listaInteracoesUsuario.stream().map(i -> new CentralGenericDTO(
                i.getId(),
                TipoItemCentral.INTERACAO,
                i.getDestinatario().getId(),
                i.getDestinatario().getNome(),
                i.getDestinatario().getFotoUrl(),
                i.getTitulo(),
                EstadoCentral.AGUARDANDO_RESPOSTA,
                i.getDataCriacao()
        )).toList();

        List<CentralGenericDTO> chamadosDto = listaChamadosDoUsuario.stream().map(c -> {
            EstadoCentral estadoCentral = (c.getStatusChamado() == StatusChamado.ABERTO)
                    ? EstadoCentral.NEGOCIANDO
                    : EstadoCentral.EM_ANDAMENTO;

            return new CentralGenericDTO(
                    c.getId(),
                    TipoItemCentral.CHAMADO,
                    c.getPrestador().getId(),
                    c.getPrestador().getNome(),
                    c.getPrestador().getFotoUrl(),
                    c.getTitulo(),
                    estadoCentral,
                    c.getDataCriacaoChamado()
            );
        }).toList();
        return Stream.concat(interacoesDto.stream(), chamadosDto.stream()).sorted(Comparator.comparing(CentralGenericDTO::dataReferencia).reversed()).toList();
    }

    public List<CentralGenericDTO> listarContatosPrestadorLogado () {
        Prestador prestador =  prestadorRepository.findById(obterIdUsuarioLogado())
                .orElseThrow(() -> new EntityNotFoundException("Impossível encontrar esse usuário"));

        List<InteracaoInicial> listaInteracoesPrestador = interacaoInicialRepository
                .findByDestinatarioAndStatus(prestador, StatusInteracao.PENDENTE);

        List<Chamado> listaChamadosDoPrestador = chamadoRepository
                .findByPrestadorAndStatusChamadoIn(prestador, List.of(StatusChamado.ABERTO, StatusChamado.EM_ANDAMENTO));

        List<CentralGenericDTO> interacoesDto = listaInteracoesPrestador.stream().map(i -> new CentralGenericDTO(
                i.getId(),
                TipoItemCentral.INTERACAO,
                i.getRemetente().getId(),
                i.getRemetente().getNome(),
                i.getRemetente().getFotoUrl(),
                i.getTitulo(),
                EstadoCentral.AGUARDANDO_RESPOSTA,
                i.getDataCriacao()
        )).toList();

        List<CentralGenericDTO> chamadosDto = listaChamadosDoPrestador.stream().map(c -> {
            EstadoCentral estadoCentral = (c.getStatusChamado() == StatusChamado.ABERTO)
                    ? EstadoCentral.NEGOCIANDO
                    : EstadoCentral.EM_ANDAMENTO;

            return new CentralGenericDTO(
                    c.getId(),
                    TipoItemCentral.CHAMADO,
                    c.getCliente().getId(),
                    c.getCliente().getNome(),
                    c.getCliente().getFotoUrl(),
                    c.getTitulo(),
                    estadoCentral,
                    c.getDataCriacaoChamado()
            );
        }).toList();
        return Stream.concat(interacoesDto.stream(), chamadosDto.stream()).sorted(Comparator.comparing(CentralGenericDTO::dataReferencia).reversed()).toList();
    }



    public InteracaoInicialResponseDTO obterDetalhesInteracao (Long id) {
        InteracaoInicial interacaoInicial = interacaoInicialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Não foi possivel encontrar detalhes dessa interação"));
        return new InteracaoInicialResponseDTO(
                interacaoInicial.getId(),
                interacaoInicial.getTitulo(),
                interacaoInicial.getMensagem(),
                interacaoInicial.getValorSugerido(),
                interacaoInicial.getDataCriacao(),
                interacaoInicial.getStatus()
        );
    }

        public List<MensagemChatDTO> carregarHistoricoMensagens (Long idChamado){
            Chamado chamado = chamadoRepository.findById(idChamado).orElseThrow(()-> new EntityNotFoundException("Impossivel encontrar esse chamado"));
            Long idUsuario = obterIdUsuarioLogado();
            if (!idUsuario.equals(chamado.getCliente().getId()) && !idUsuario.equals(chamado.getPrestador().getId())){
                throw new IllegalArgumentException("Impossivel acessar a conversa de outros");
            }
            List<MensagemChat> mensagens = mensagemChatRepository.findByChamadoOrderByDataEnvioAsc(chamado);
            return mensagens.stream().map(m->{
                return new MensagemChatDTO(
                        m.getId(),
                        m.getChamado().getId(),
                        m.getRemetente().getId(),
                        m.getRemetente().getNome(),
                        m.getRemetente().getFotoUrl(),
                        m.getConteudo(),
                        m.getDataEnvio(),
                        m.isLida()
                );
            }).toList();
        }


    public MensagemChatDTO receberMensagem(Long idChamado, Long idUsuario, @Valid MensagemChatConteudoDTO dto) {

        if (idChamado == null || idUsuario == null){
            throw new IllegalArgumentException("id nulo, impossivel continuar");
        }

        Chamado chamado = chamadoRepository.findById(idChamado).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse chamado"));
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new EntityNotFoundException("Impossivem encontrar esse usuario"));

        if (!idUsuario.equals(chamado.getCliente().getId())
                && !idUsuario.equals(chamado.getPrestador().getId())) {
            throw new IllegalArgumentException("Você está tentando acessar um chamado que não pertence a você");
        }

        MensagemChat mensagemChat = new MensagemChat();
        mensagemChat.setChamado(chamado);
        mensagemChat.setRemetente(usuario);
        mensagemChat.setConteudo(dto.conteudoMensagem());

        mensagemChat = mensagemChatRepository.save(mensagemChat);

        return new MensagemChatDTO(
                mensagemChat.getId(),
                mensagemChat.getChamado().getId(),
                mensagemChat.getRemetente().getId(),
                mensagemChat.getRemetente().getNome(),
                mensagemChat.getRemetente().getFotoUrl(),
                mensagemChat.getConteudo(),
                mensagemChat.getDataEnvio(),
                false
        );

    }

    public List<CentralGenericDTO> decidirListarContatosDeQuem() {

        List<CentralGenericDTO> lista = new ArrayList<>();

        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_USUARIO"))){
           lista = listarContatosUsuarioLogado();
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(p -> Objects.equals(p.getAuthority(), "ROLE_PRESTADOR"))) {
           lista = listarContatosPrestadorLogado();
        }

        return lista;

    }
}
