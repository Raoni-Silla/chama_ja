package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Servico.ServicoResponseDTO;
import com.raoni.chamaja.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping(path = "/listar-servicos")
    @PreAuthorize("hasAnyRole('USUARIO','PRESTADOR')")
    public ResponseEntity<List<ServicoResponseDTO>> listarServicos() {
        List<ServicoResponseDTO> servicos = servicoService.listarServicos();
        return ResponseEntity.ok(servicos);
    }


    @PatchMapping(path = "concluir-servico/{idChamado}")
    @PreAuthorize("hasAnyRole('USUARIO','PRESTADOR')")
    public ResponseEntity<Void> concluirServico (@PathVariable(name = "idChamado") Long idChamado) {
        servicoService.confirmarConclusao(idChamado);
        return ResponseEntity.noContent().build();
    }

}
