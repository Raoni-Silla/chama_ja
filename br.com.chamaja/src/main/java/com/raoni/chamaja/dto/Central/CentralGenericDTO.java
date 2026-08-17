package com.raoni.chamaja.dto.Central;

import com.raoni.chamaja.enums.EstadoCentral;
import com.raoni.chamaja.enums.TipoItemCentral;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CentralGenericDTO(
        @NotNull
        Long idReferencia,
        @NotNull
        TipoItemCentral tipoItemCentral,
        @NotNull
        Long idOutraPessoa,
        @NotBlank
        String nomeOutraPessoa,
        String fotoOutraPessoa,
        @NotBlank
        String titulo,
        @NotNull
        EstadoCentral estadoCentral,
        LocalDateTime dataReferencia
) {
}
