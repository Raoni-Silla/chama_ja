package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Categoria.CategoriaDetalhesDTO;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoIniciaInfoUteisParaPrestador;
import com.raoni.chamaja.dto.Prestador.CarregarAreasAtuacaoPrestador;
import com.raoni.chamaja.dto.Prestador.CarregarHomePrestadorDTO;
import com.raoni.chamaja.dto.Prestador.MelhoresDoMesDTO;
import com.raoni.chamaja.dto.Prestador.PrestadorResponseDTO;
import com.raoni.chamaja.dto.Servico.ServicoSimplificadoDTO;
import com.raoni.chamaja.model.Categoria;
import com.raoni.chamaja.model.Endereco;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.projection.PrestadorProximoProjection;
import com.raoni.chamaja.repository.CategoriaRepository;
import com.raoni.chamaja.repository.EnderecoRepository;
import com.raoni.chamaja.repository.PrestadorRepository;
import com.raoni.chamaja.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestadorService {

    private final PrestadorRepository prestadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final ServicoService servicoService;
    private final InteracaoInicialService interacaoInicialService;
    private final CategoriaRepository categoriaRepository;

    private PrestadorResponseDTO converterParaDTO(
            Prestador prestador,
            Double distanciaKm
    ) {

        String cidade = prestador.getEnderecos().stream()
                .filter(Endereco::isEnderecoPrincipal)
                .map(Endereco::getNomeCidade)
                .findFirst()
                .orElse("Cidade não informada");

        List<String> nomeCategorias = prestador.getCategorias().stream()
                .map(Categoria::getNome)
                .collect(Collectors.toList());

        return new PrestadorResponseDTO(
                prestador.getId(),
                prestador.getNome(),
                prestador.getFotoUrl(),
                prestador.getBiografia(),
                prestador.getNotaMedia(),
                prestador.getValorHora(),
                cidade,
                nomeCategorias,
                distanciaKm
        );
    }

    private Long obterIdUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private MelhoresDoMesDTO converterDtoMelhorMes(Prestador prestador, Double distanciaKm) {

        List<String> nomeCategorias = prestador.getCategorias().stream()
                .map(Categoria::getNome)
                .toList();

        return new MelhoresDoMesDTO(
                prestador.getId(),
                prestador.getNome(),
                prestador.getFotoUrl(),
                nomeCategorias,
                prestador.getNotaMedia(),
                prestador.isVerificado(),
                prestador.getServicosConcluidos(),
                distanciaKm
        );

    }

    public List<PrestadorResponseDTO> buscarPrestadores(String termo) {

        Long usuarioId = obterIdUsuarioLogado();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Impossível encontrar usuário logado"
                        )
                );

        if (termo == null || termo.trim().isEmpty()) {

            return prestadorRepository.findAll()
                    .stream()
                    .map(prestador -> converterParaDTO(prestador, null))
                    .toList();
        }

        double raioMetros =
                usuario.getRaioDeBusca() == null
                        ? 30000
                        : usuario.getRaioDeBusca();

        List<PrestadorProximoProjection> prestadoresProximos =
                prestadorRepository.findIdsByBuscaInteligente(
                        termo,
                        usuarioId,
                        raioMetros
                );

        if (prestadoresProximos.isEmpty()) {
            return List.of();
        }

        List<Long> ids = prestadoresProximos.stream()
                .map(PrestadorProximoProjection::getId)
                .toList();

        Map<Long, Prestador> prestadoresPorId =
                prestadorRepository.findAllById(ids)
                        .stream()
                        .collect(Collectors.toMap(
                                Prestador::getId,
                                Function.identity()
                        ));

        return prestadoresProximos.stream()
                .map(projection -> {

                    Prestador prestador =
                            prestadoresPorId.get(projection.getId());

                    if (prestador == null) {
                        throw new EntityNotFoundException(
                                "Prestador não encontrado: "
                                        + projection.getId()
                        );
                    }

                    return converterParaDTO(
                            prestador,
                            projection.getDistanciaKm()
                    );
                })
                .toList();
    }


    public PrestadorResponseDTO detalharInformacoesPrestador(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Impossível buscar um id nulo");
        }

        Prestador prestador = prestadorRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Impossível encontrar esse prestador"
                        )
                );

        Long usuarioId = obterIdUsuarioLogado();

        Double distanciaKm = enderecoRepository
                .calcularDistanciaKm(id, usuarioId)
                .orElse(null);

        return converterParaDTO(
                prestador,
                distanciaKm
        );
    }

    public List<MelhoresDoMesDTO> top4MelhoresPrestadores() {

        Long usuarioId = obterIdUsuarioLogado();

        List<PrestadorProximoProjection> melhores =
                prestadorRepository.findTop4Melhores(usuarioId);

        if (melhores.isEmpty()) {
            return List.of();
        }

        List<Long> ids = melhores.stream()
                .map(PrestadorProximoProjection::getId)
                .toList();

        Map<Long, Prestador> prestadoresPorId =
                prestadorRepository.findAllById(ids)
                        .stream()
                        .collect(Collectors.toMap(
                                Prestador::getId,
                                Function.identity()
                        ));

        return melhores.stream()
                .map(projection -> {

                    Prestador prestador =
                            prestadoresPorId.get(projection.getId());

                    if (prestador == null) {
                        throw new EntityNotFoundException(
                                "Prestador não encontrado: " + projection.getId()
                        );
                    }

                    return converterDtoMelhorMes(
                            prestador,
                            projection.getDistanciaKm()
                    );
                })
                .toList();
    }


    public CarregarHomePrestadorDTO carregarHomePrestador() {
        Prestador prestador = prestadorRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar algum prestador logado"));
        ServicoSimplificadoDTO servicoSimplificadoDTO = servicoService.obterProximoServico(prestador);
        List<InteracaoIniciaInfoUteisParaPrestador> interacoesPendentes = interacaoInicialService.obterListaDeInteracoesPendentes();
        String nomeCidadeEnderecoPrincipal = prestador.getEnderecos().stream().filter(Endereco::isEnderecoPrincipal).toList().getFirst().getNomeCidade();
        return new CarregarHomePrestadorDTO(
                prestador.getNome(),
                nomeCidadeEnderecoPrincipal,
                servicoSimplificadoDTO,
                interacoesPendentes
        );
    }

    public CarregarAreasAtuacaoPrestador carregarAreasAtuacaoPrestador () {
        Prestador prestador = prestadorRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar algum prestador logado"));
        List<CategoriaDetalhesDTO> listaCategoriasPrestador = prestador.getCategorias().stream().map(c -> {
            return new CategoriaDetalhesDTO(
                    c.getId(),
                    c.getNome(),
                    c.getIconUrl()
            );
        }).toList();
        List<CategoriaDetalhesDTO> listaCategoriasDisponiveis = categoriaRepository.findByIdNotIn(prestador.getCategorias().stream().map(Categoria::getId).toList()).stream().map(c -> {
            return new CategoriaDetalhesDTO(
                    c.getId(),
                    c.getNome(),
                    c.getIconUrl()
            );
        }).toList();
        return  new CarregarAreasAtuacaoPrestador(
                listaCategoriasPrestador,
                listaCategoriasDisponiveis
        );
    }

    public void adicionarCategoria(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Id inválido");
        }
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada por id"));
        Prestador prestador = prestadorRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar algum prestador logado"));
        prestador.getCategorias().add(categoria);
        prestadorRepository.save(prestador);
    }

    public void removerCategoria(Long idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Id inválido");
        }
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada por id"));
        Prestador prestador = prestadorRepository.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar algum prestador logado"));
        prestador.getCategorias().remove(categoria);
        prestadorRepository.save(prestador);
    }
}
