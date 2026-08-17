package com.raoni.chamaja.service;

import com.raoni.chamaja.dto.Endereco.EnderecoResponseDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioInfoBasicasDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioInfoPerfilDTO;
import com.raoni.chamaja.dto.Usuario.UsuarioTrocaSenhaDTO;
import com.raoni.chamaja.model.Endereco;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository userRepo;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;

    private String gerarNumeroAleatorioValidacao() {
        int codigo = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(codigo);
    }

    private String formatarTelefoneProTwilio(String telefoneDigitado) {
        String numeroLimpo = telefoneDigitado.replaceAll("\\D", "");

        if (numeroLimpo.startsWith("55") && numeroLimpo.length() == 13) {
            return "+" + numeroLimpo;
        }

        if (numeroLimpo.length() == 11) {
            return "+55" + numeroLimpo;
        }

        throw new IllegalArgumentException("O número digitado está incompleto ou inválido. Por favor, inclua o DDD.");
    }

    private Long obterIdUsuarioLogado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    public UsuarioInfoBasicasDTO obterNomeAndEnderecoDoUsuarioLogado() {
        Long idSeguro = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(idSeguro).orElseThrow(() -> new RuntimeException("Impossivel encontrar esse usuario"));
        String nome = usuario.getNome();
        String cidadePrincipal = usuario.getEnderecos().stream()
                .filter(Endereco::isEnderecoPrincipal)
                .map(Endereco::getNomeCidade)
                .findFirst()
                .orElse("Localização não informada");
        String estado = usuario.getEnderecos().stream()
                .filter(Endereco::isEnderecoPrincipal)
                .map(Endereco::getSiglaEstado)
                .findFirst()
                .orElse("Localização não informada");
        return new UsuarioInfoBasicasDTO(nome, cidadePrincipal, estado);
    }


    public UsuarioInfoPerfilDTO obterInformacoesDoPerfilUsuario() {
        Long idSeguro = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(idSeguro).orElseThrow(() -> new RuntimeException("Impossivel encontrar esse usuario"));
        String ultimosDoisDigitosCpf = usuario.getCpf().substring(usuario.getCpf().length() - 2);
        return new UsuarioInfoPerfilDTO(usuario.getNome(), usuario.getEmail(), usuario.getTelefone(), usuario.getFotoUrl(), ultimosDoisDigitosCpf, usuario.isVerificado());
    }

    @Transactional
    public UsuarioInfoPerfilDTO salvarModificacoesDasInformacoesDePerfil(@Valid UsuarioInfoPerfilDTO dto) {
        Long idSeguro = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(idSeguro).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        usuario.setEmail(dto.email());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setFotoUrl(dto.urlFoto());
        usuario.setNome(dto.nome());
        usuario = userRepo.save(usuario);
        return new UsuarioInfoPerfilDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getFotoUrl(),
                usuario.getCpf(),
                usuario.isVerificado());
    }

    @Transactional
    public void solicitarTrocaDeTelefoneAndEnvioDeSms(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        Long id = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        String codigoGerado = gerarNumeroAleatorioValidacao();
        usuario.setCodigoSms(codigoGerado);
        String telefoneFormatadoParaTwillio = formatarTelefoneProTwilio(telefone);

        try {
            smsService.enviarSms(telefoneFormatadoParaTwillio, codigoGerado);
            usuario.setTelefonePendente(telefoneFormatadoParaTwillio);
            userRepo.save(usuario);
        } catch (Exception e) {
            usuario.setCodigoSms(null);
            userRepo.save(usuario);
            throw new RuntimeException("Erro ao enviar SMS: " + e.getMessage());
        }
    }

    @Transactional
    public void confirmarCodigoSms(String codigoDigitadoPeloUsuario) {
        Long id = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));

        if (Objects.equals(usuario.getCodigoSms(), codigoDigitadoPeloUsuario)) {
            usuario.setTelefone(usuario.getTelefonePendente());
            usuario.setTelefonePendente(null);
            usuario.setCodigoSms(null);
            userRepo.save(usuario);
        } else {
            throw new RuntimeException("Código de verificação inválido!");
        }
    }

    @Transactional
    public void trocarSenha(UsuarioTrocaSenhaDTO dto) {
        if (dto.senhaAntiga().isBlank() || dto.senhaNova().isBlank()) {
            throw new IllegalArgumentException("Impossivel continuar operação, insira dados validos");
        }
        Usuario usuario = userRepo.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Impossivel encontrar esse usuario"));
        if (!passwordEncoder.matches(dto.senhaAntiga(), usuario.getSenha())) {
            throw new RuntimeException("senha inválida");
        }
        usuario.setSenha(passwordEncoder.encode(dto.senhaNova()));
        userRepo.save(usuario);
    }

    public String obterCpf() {
        Long id = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));
        return usuario.getCpf();
    }

    @Transactional
    public void desativarConta() {
        Long id = obterIdUsuarioLogado();
        Usuario usuario = userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));
        usuario.setContaAtiva(false);
        userRepo.save(usuario);
    }

    @Transactional
    public void excluirConta() {
        Long id = obterIdUsuarioLogado();
        userRepo.deleteById(id);
    }

    public List<EnderecoResponseDTO> listarTodosEnderecosUsuario() {
        Usuario usuario = userRepo.findById(obterIdUsuarioLogado()).orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
        return usuario.getEnderecos().stream().map(e -> {
            return new EnderecoResponseDTO(
                    e.getId(),
                    e.getLogradouro(),
                    e.getNumero(),
                    e.getComplemento(),
                    e.getNomeCidade(),
                    e.getSiglaEstado(),
                    e.getCep(),
                    e.getLatitude(),
                    e.getLongitude(),
                    e.isEnderecoPrincipal()
            );
        }).toList();
    }


}
