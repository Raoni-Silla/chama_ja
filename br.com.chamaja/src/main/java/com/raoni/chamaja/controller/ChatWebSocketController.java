package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Chat.MensagemChatConteudoDTO;
import com.raoni.chamaja.dto.Chat.MensagemChatDTO;
import com.raoni.chamaja.service.CentralChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final CentralChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chamado/{idChamado}/mensagem")
    public void receberMensagem(
            @DestinationVariable Long idChamado,
            @Valid MensagemChatConteudoDTO dto,
            Principal principal) {
        Long idUsuario = Long.parseLong(principal.getName());
        MensagemChatDTO dtoResposta = chatService.receberMensagem(idChamado,idUsuario, dto);
        messagingTemplate.convertAndSend("/topic/chamado/" + dtoResposta.idChamado(),dtoResposta);

    }

}
