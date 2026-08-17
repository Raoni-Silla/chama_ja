package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Login.LoginRequestDTO;
import com.raoni.chamaja.dto.Login.LoginResponseDTO;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.repository.UsuarioRepository;
import com.raoni.chamaja.seguranca.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos"));

//        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
//            throw new RuntimeException("Usuário ou senha inválidos");
//        }

        if(!usuario.isContaAtiva()){
            usuario.setContaAtiva(true);
            usuarioRepository.save(usuario);
        }

        String token = jwtService.gerarTokenDefinitivo(usuario);

        return new LoginResponseDTO(token);
    }
}