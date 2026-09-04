package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Pagamento.PagamentoRequestDTO;
import com.raoni.chamaja.repository.PagamentoRepository;
import com.raoni.chamaja.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/pagamentos")
@RequiredArgsConstructor
public class pagamentoController {

    private final PagamentoService pagamentoService;

    @PatchMapping(path = "/pagar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> pagar(@RequestBody @Valid  PagamentoRequestDTO pagamentoRequestDTO) {
        pagamentoService.pagar(pagamentoRequestDTO);
        return ResponseEntity.noContent().build();
    }

}
