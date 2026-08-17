package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Endereco.EnderecoRequestDTO;
import com.raoni.chamaja.dto.Endereco.EnderecoResponseDTO;
import com.raoni.chamaja.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping(path = ("/salvar-endereco"))
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> salvarEndereco (@RequestBody EnderecoRequestDTO dto){
        enderecoService.salvarEndereco(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obter-enderecos")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<EnderecoResponseDTO>> obterEnderecos (){
        List<EnderecoResponseDTO> responseDTOS = enderecoService.listarEnderecos();
        return ResponseEntity.ok(responseDTOS);
    }

    @DeleteMapping(path = "/deletar-endereco/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> excluirEndereco (@PathVariable(name = "id") Long id) {
        enderecoService.excluirEndereco(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping(path = "/atualizar-endereco/{id}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> atualizarEndereco (@PathVariable(name = "id") Long id, @RequestBody EnderecoRequestDTO dto) {
        enderecoService.atualizarInformacoesEndereco(id,dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/definir-endereco-principal/{id}")
    @PreAuthorize("hasRole('USUARIO')") //prestador tambem pode
    public ResponseEntity<Void> definirEnderecoPrincipal (@PathVariable (name = "id") Long id){
        enderecoService.definirNovoEnderecoPrincipal(id);
        return ResponseEntity.status(200).build();
    }


}
