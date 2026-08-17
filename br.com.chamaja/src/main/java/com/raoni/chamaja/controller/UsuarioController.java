package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Endereco.EnderecoResponseDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioInfoBasicasDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioInfoPerfilDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioTrocaSenhaDTO;
import com.raoni.chamaja.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping(path = "/api/usuarios")
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping(path = "obter-infos-basicas")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<UsuarioInfoBasicasDTO> obterInfosBasicasUsuarioLogado(){
        UsuarioInfoBasicasDTO resposta = usuarioService.obterNomeAndEnderecoDoUsuarioLogado();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping(path = "obter-infos-perfil")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<UsuarioInfoPerfilDTO> obterInfosParaTelaDePerfil(){
        UsuarioInfoPerfilDTO resposta = usuarioService.obterInformacoesDoPerfilUsuario();
        return ResponseEntity.ok(resposta);
    }

    @PostMapping(path = "salvar-modificacoes")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<UsuarioInfoPerfilDTO> salvarModificacoesDasInformacoesDePerfil (@Valid @RequestBody UsuarioInfoPerfilDTO dto){
        UsuarioInfoPerfilDTO resposta = usuarioService.salvarModificacoesDasInformacoesDePerfil(dto);
        return ResponseEntity.status(201).body(resposta);
    }

    @PostMapping(path = "mudar-telefone/{telefone}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> solicitarTrocaDeTelefoneAndEnvioDeSms (@PathVariable("telefone") String telefone){
        usuarioService.solicitarTrocaDeTelefoneAndEnvioDeSms(telefone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "confirmar-codigo/{codigo}")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> confirmarCodigoSms(@PathVariable("codigo") String codigo){
        usuarioService.confirmarCodigoSms(codigo);
        return ResponseEntity.ok().build();
    }

    @PostMapping (path = "mudar-senha")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> trocarSenha (@Valid @RequestBody UsuarioTrocaSenhaDTO dto){
        usuarioService.trocarSenha(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping (path = "/obter-cpf")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity <Map<String,String>> obterCpfUsuarioLogado (){
       String cpf = usuarioService.obterCpf();
        Map<String, String> map = Map.of("cpf", cpf);
        return ResponseEntity.ok(map);
    }

    @PostMapping(path = "/desativar")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> desativarConta () {
        usuarioService.desativarConta();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/excluir")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<Void> excluirConta () {
        usuarioService.excluirConta();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/obter-enderecos")
    @PreAuthorize("hasRole('USUARIO')")
    public ResponseEntity<List<EnderecoResponseDTO>> obterTodosEnderecos () {
        List<EnderecoResponseDTO> listDto = usuarioService.listarTodosEnderecosUsuario();
        return ResponseEntity.status(200).body(listDto);
    }
}
