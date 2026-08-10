package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Prestador.MelhoresDoMesDTO;
import com.raoni.chamaja.dto.Prestador.PrestadorResponseDTO;
import com.raoni.chamaja.model.Categoria;
import com.raoni.chamaja.model.Endereco;
import com.raoni.chamaja.model.Prestador;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.projection.PrestadorProximoProjection;
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

    private Long obterIdUsuarioLogado (){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private MelhoresDoMesDTO converterDtoMelhorMes (Prestador prestador, Double distanciaKm){

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

    public List<MelhoresDoMesDTO> top5MelhoresPrestadores() {

        Long usuarioId = obterIdUsuarioLogado();

        List<PrestadorProximoProjection> melhores =
                prestadorRepository.findTop5Melhores(usuarioId);

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
}
