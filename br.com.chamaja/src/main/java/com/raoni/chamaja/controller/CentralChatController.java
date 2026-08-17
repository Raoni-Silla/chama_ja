package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Central.CentralGenericDTO;
import com.raoni.chamaja.dto.Chat.MensagemChatDTO;
import com.raoni.chamaja.dto.InteracaoInicial.InteracaoInicialResponseDTO;
import com.raoni.chamaja.service.CentralChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/chat")
@RequiredArgsConstructor
public class CentralChatController {

    private final CentralChatService centralChatService;

    @GetMapping(path = "/obter-contatos")
    @PreAuthorize("hasAnyRole('USUARIO', 'PRESTADOR')")
    public ResponseEntity<List<CentralGenericDTO>> obterContatosUsuario () {
        List<CentralGenericDTO> responseList = centralChatService.decidirListarContatosDeQuem();
        return ResponseEntity.status(200).body(responseList);
    }

    @GetMapping(path = "/obter-detalhes-interacao/{id}")
    @PreAuthorize("hasAnyRole('USUARIO','PRESTADOR')")
    public ResponseEntity<InteracaoInicialResponseDTO> obterDetalhesInteracao (@PathVariable(name = "id") Long id) {
        InteracaoInicialResponseDTO resposta = centralChatService.obterDetalhesInteracao(id);
        return ResponseEntity.status(200).body(resposta);
    }

    @GetMapping(path = "/chamado/{idChamado}/mensagens")
    @PreAuthorize("hasAnyRole('USUARIO','PRESTADOR')")
    public ResponseEntity<List<MensagemChatDTO>> obterMensagens (@PathVariable(name = "idChamado") Long idChamado){
        List<MensagemChatDTO> mensagens = centralChatService.carregarHistoricoMensagens(idChamado);
        return ResponseEntity.status(200).body(mensagens);
    }

}
