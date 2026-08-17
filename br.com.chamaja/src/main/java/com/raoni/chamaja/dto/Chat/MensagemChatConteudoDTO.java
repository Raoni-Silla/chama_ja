package com.raoni.chamaja.dto.Chat;

import jakarta.validation.constraints.NotBlank;

public record MensagemChatConteudoDTO(
        @NotBlank
        String conteudoMensagem
) {
}
