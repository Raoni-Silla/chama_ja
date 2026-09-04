package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Proposta.PropostaRequestDTO;
import com.raoni.chamaja.dto.Proposta.PropostaResponseDTO;
import com.raoni.chamaja.repository.PrestadorRepository;
import com.raoni.chamaja.service.PrestadorService;
import com.raoni.chamaja.service.PropostaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/proposta")
public class PropostaController {

    private final PropostaService service;

    @PostMapping(path = "/chamado/{idChamado}")
    @PreAuthorize("hasRole('PRESTADOR')")
    public ResponseEntity<PropostaResponseDTO> criarProposta (@Valid @RequestBody PropostaRequestDTO dto, @PathVariable(name = "idChamado") Long idChamado){
        PropostaResponseDTO responseDto = service.criarProposta(idChamado, dto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping(path = "/chamado/{idChamado}/pendente")
    @PreAuthorize("hasAnyRole('USUARIO','PRESTADOR')")
    public ResponseEntity<PropostaResponseDTO> obterPropostaPendente (@PathVariable(name = "idChamado") Long idChamado){
        PropostaResponseDTO responseDto = service.obterPropostaPendente(idChamado);
        return ResponseEntity.status(200).body(responseDto);
    }

    @PatchMapping(path = "/recusar-proposta/{idProposta}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> recusarProposta (@PathVariable(name = "idProposta") Long idProposta){
        service.recusarProposta(idProposta);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/aceitar-proposta/{idProposta}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> aceitarProposta (@PathVariable(name = "idProposta") Long idProposta){
        service.aceitarProposta(idProposta);
        return ResponseEntity.noContent().build();
    }

}
