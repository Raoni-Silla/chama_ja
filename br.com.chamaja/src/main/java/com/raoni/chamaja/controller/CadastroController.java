package com.raoni.chamaja.controller;

import com.raoni.chamaja.dto.Cadastro.CadastroRequestDTO;
import com.raoni.chamaja.dto.Cadastro.CadastroResponseDTO;
import com.raoni.chamaja.dto.Endereco.EnderecoRequestDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioResponseDTO;
import com.raoni.chamaja.model.CadastroTemporario;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.seguranca.JwtService;
import com.raoni.chamaja.service.CadastroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/registro")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CadastroController {

    private final CadastroService cadastroService;
    private final JwtService jwt;

    @PostMapping("/iniciar")
    public ResponseEntity<Map<String, String>> iniciarCadastro(
            @RequestBody CadastroRequestDTO dto) {

        CadastroTemporario cadastro = cadastroService.iniciarCadastro(dto);
        String tokenTemporario = jwt.gerarToken(String.valueOf(cadastro.getId()));

        Map<String, String> response = new HashMap<>();
        response.put("token", tokenTemporario);
        response.put("status", cadastro.getStatus().name());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/telefone")
    public ResponseEntity<CadastroResponseDTO> adicionarTelefone(
            @RequestParam String telefone) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long idSeguro = Long.parseLong(authentication.getName());

        CadastroTemporario cadastro = cadastroService.adicionarTelefone(idSeguro, telefone);

        CadastroResponseDTO response = new CadastroResponseDTO(
                cadastro.getId(),
                cadastro.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/telefone/solicitar-envio-sms")
    public ResponseEntity<CadastroResponseDTO> solicitarEnvioSMS() {


        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long idSeguro = Long.parseLong(authentication.getName());

        CadastroTemporario cadastro = cadastroService.solicitarEnvioSms(idSeguro);

        CadastroResponseDTO response = new CadastroResponseDTO(
                cadastro.getId(),
                cadastro.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/telefone/confirmar-codigo-sms")
    public ResponseEntity<CadastroResponseDTO> confirmarCodigoSMS(
            @RequestParam String codigo) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long idSeguro = Long.parseLong(authentication.getName());

        CadastroTemporario cadastro = cadastroService.confirmarCodigoSms(idSeguro,codigo);

        CadastroResponseDTO response = new CadastroResponseDTO(
                cadastro.getId(),
                cadastro.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/confirmar-endereco")
    public ResponseEntity<Void> cadastrarEndereco (@Valid @RequestBody EnderecoRequestDTO dto){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long idSeguro = Long.parseLong(authentication.getName());
        cadastroService.cadastrarEndereco(dto, idSeguro);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/tipo-usuario")
    public ResponseEntity<UsuarioResponseDTO> tipoUsuario(
            @RequestParam String tipoUsuario) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Long idSeguro = Long.parseLong(authentication.getName());

        Usuario usuario = cadastroService.usuarioOuPrestador(idSeguro,tipoUsuario);

        String tokenDefinitivo = jwt.gerarTokenDefinitivo(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getNotaMedia(),
                usuario.getRaioDeBusca(),
                tokenDefinitivo
        );

        return ResponseEntity.ok(response);
    }



}
