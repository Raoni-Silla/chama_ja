package com.raoni.chamaja.dto.Chat;

import java.time.LocalDateTime;

public record MensagemChatDTO(
        Long idMensagem,
        Long idChamado,
        Long idRemetente,
        String nomeRemetente,
        String fotoRemetente,
        String conteudo,
        LocalDateTime dataEnvio,
        boolean lida
) {
}
