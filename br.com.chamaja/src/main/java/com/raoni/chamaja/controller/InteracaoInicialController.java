package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.InteracaoInicial.InteracaoIniciaInfoUteisParaPrestador;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialRequestDTO;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialResponseDTO;
import com.raoni.chamaja.repository.InteracaoInicialRepository;
import com.raoni.chamaja.service.InteracaoInicialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/interacao")
public class InteracaoInicialController {

    private final InteracaoInicialService service;


    @PostMapping(path = "/criar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<InteracaoInicialResponseDTO> criarInteracaoInicial (@Valid @RequestBody InteracaoInicialRequestDTO dto) {
        InteracaoInicialResponseDTO responseDto = service.iniciarInteracaoUsuarioParaPrestador(dto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping(path = "/obter-interacoes-pendentes")
//    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<List<InteracaoIniciaInfoUteisParaPrestador>> obterInteracoesPendentes (){
        List<InteracaoIniciaInfoUteisParaPrestador> infosList = service.obterListaDeInteracoesPendentes();
        return ResponseEntity.status(200).body(infosList);
    }

    @PatchMapping(path = "/comecar-negociacao/{id}")
//    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<Long> comecarNegociacao (@PathVariable Long id) {
        Long idChamado = service.comecarNegociacao(id);
        return ResponseEntity.status(201).body(idChamado);
    }

}
