package com.raoni.chamaja.service;

import com.raoni.chamaja.Erros.EntityAlreadyExistsException;
import com.raoni.chamaja.dto.Cadastro.CadastroRequestDTO;
import com.raoni.chamaja.dto.Endereco.EnderecoRequestDTO;
import com.raoni.chamaja.enums.StatusCadastro;
import com.raoni.chamaja.enums.TipoUsuario;
import com.raoni.chamaja.model.CadastroTemporario;
import com.raoni.chamaja.model.Endereco;
import com.raoni.chamaja.model.EnderecoTemporario;
import com.raoni.chamaja.model.Usuario;
import com.raoni.chamaja.repository.CadastroTemporarioRepository;
import com.raoni.chamaja.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class CadastroService {

    private final UsuarioRepository userRepo;
    private final CadastroTemporarioRepository cadastroTemporarioRepository;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;

    private String normalizarNome(String nome) {
        nome = nome.trim().toLowerCase();

        String[] partes = nome.split(" ");
        StringBuilder resultado = new StringBuilder();

        for (String parte : partes) {
            if (!parte.isEmpty()) {
                resultado.append(Character.toUpperCase(parte.charAt(0)))
                        .append(parte.substring(1))
                        .append(" ");
            }
        }

        return resultado.toString().trim();
    }

    private String gerarNumeroAleatorioValidacao() {
        int codigo = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(codigo);
    }

    private boolean isCpfValido(String cpf) {

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = (int) (cpf.charAt(i) - '0');
                soma += (num * peso);
                peso--;
            }

            int resto = 11 - (soma % 11);
            int primeiroDigitoCalculado = (resto == 10 || resto == 11) ? 0 : resto;

            if (primeiroDigitoCalculado != (int) (cpf.charAt(9) - '0')) {
                return false;
            }

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = (int) (cpf.charAt(i) - '0');
                soma += (num * peso);
                peso--;
            }

            resto = 11 - (soma % 11);
            int segundoDigitoCalculado = (resto == 10 || resto == 11) ? 0 : resto;

            return segundoDigitoCalculado == (int) (cpf.charAt(10) - '0');

        } catch (Exception e) {
            return false;
        }
    }

    private boolean validarDataNascimento(LocalDate data) {
        if (data == null) return false;

        LocalDate hoje = LocalDate.now();

        if (data.isAfter(hoje)) return false;

        if (data.isBefore(LocalDate.of(1900, 1, 1))) return false;

        int idade = Period.between(data, hoje).getYears();

        return idade >= 18;
    }

    //padrão PRECISA ser +5518996985449 pro twillo entender
    private String formatarTelefoneProTwilio(String telefoneDigitado) {
        String numeroLimpo = telefoneDigitado.replaceAll("\\D", "");

        // o número limpo terá 13 dígitos. A gente só põe o "+" na frente.
        // 2. Se o usuário já digitou o 55 no começo (ex: 55 18 99698-5449),
        if (numeroLimpo.startsWith("55") && numeroLimpo.length() == 13) {
            return "+" + numeroLimpo;
        }

        // 3. Se o número limpo tem 11 dígitos (DDD + 9 dígitos do celular)
        if (numeroLimpo.length() == 11) {
            return "+55" + numeroLimpo;
        }

        throw new IllegalArgumentException("O número digitado está incompleto ou inválido. Por favor, inclua o DDD.");
    }

    private void validarEmailDuplicado (String email){
        if (userRepo.existsByEmail(email)){
            throw new EntityAlreadyExistsException("Email já existente no ChamaJá");
        }
    }

    private void validarCpfDuplicado (String cpf){
        if (userRepo.existsByCpf(cpf)){
            throw new EntityAlreadyExistsException("Cpf já existente no ChamaJá");
        }
    }

    private void validarTelefoneDuplicado (String telefone){
        if (userRepo.existsByCpf(telefone)){
            throw new EntityAlreadyExistsException("Telefone já existente no ChamaJá");
        }
    }


    @Transactional
    public CadastroTemporario iniciarCadastro(CadastroRequestDTO dto) {
        CadastroTemporario cadastro = new CadastroTemporario();

        validarEmailDuplicado(dto.email());
        validarCpfDuplicado(dto.cpf());


        cadastro.setNome(normalizarNome(dto.nome()));
        cadastro.setEmail(dto.email().trim().toLowerCase());
        if (!isCpfValido(dto.cpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
        cadastro.setCpf(dto.cpf());
        if (!validarDataNascimento(dto.dataNascimento())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
        cadastro.setDataNascimento(dto.dataNascimento());
        cadastro.setSenha(passwordEncoder.encode(dto.senha()));
        cadastro.setStatus(StatusCadastro.INICIADO);


        return cadastroTemporarioRepository.save(cadastro);
    }

    @Transactional
    public CadastroTemporario adicionarTelefone(Long cadastroId, String telefone) {
        CadastroTemporario cadastroTemporario = cadastroTemporarioRepository.findById(cadastroId).orElseThrow(() -> new EntityNotFoundException("Não Encontramos o seu usuario"));
        validarTelefoneDuplicado(formatarTelefoneProTwilio(telefone));
        cadastroTemporario.setStatus(StatusCadastro.AGUARDANDO_TELEFONE);
        cadastroTemporario.setTelefone(formatarTelefoneProTwilio(telefone));
        return cadastroTemporarioRepository.save(cadastroTemporario);
    }

    @Transactional
    public CadastroTemporario solicitarEnvioSms(Long cadastroId) {
        CadastroTemporario cadastroTemporario = cadastroTemporarioRepository.findById(cadastroId)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));

        String codigoGerado = gerarNumeroAleatorioValidacao();

        cadastroTemporario.setCodigoSms(codigoGerado);
        cadastroTemporario.setStatus(StatusCadastro.AGUARDANDO_SMS);

        try {
            smsService.enviarSms(cadastroTemporario.getTelefone(), codigoGerado);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar SMS: " + e.getMessage());
        }

        return cadastroTemporarioRepository.save(cadastroTemporario);
    }

    @Transactional
    public CadastroTemporario confirmarCodigoSms(Long cadastroId, String codigoDigitadoPeloUsuario) {
        CadastroTemporario cadastroTemporario = cadastroTemporarioRepository.findById(cadastroId)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));

        if (cadastroTemporario.getCodigoSms().equals(codigoDigitadoPeloUsuario)) {
            cadastroTemporario.setStatus(StatusCadastro.TELEFONE_VALIDADO);
            cadastroTemporario.setTelefoneValidado(true);
            return cadastroTemporarioRepository.save(cadastroTemporario);
        } else {
            throw new RuntimeException("Código de verificação inválido!");
        }
    }


    @Transactional
    public Usuario usuarioOuPrestador(Long cadastroId, String tipoUsuario) {

        CadastroTemporario cadastro = cadastroTemporarioRepository.findById(cadastroId)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));

        TipoUsuario tipoEnum;
        try {
            tipoEnum = TipoUsuario.valueOf(tipoUsuario.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de usuário inválido: " + tipoUsuario);
        }

        Usuario usuario = new Usuario();
        usuario.setNome(cadastro.getNome());
        usuario.setEmail(cadastro.getEmail());
        usuario.setSenha(cadastro.getSenha());
        usuario.setCpf(cadastro.getCpf());
        usuario.setTipoUsuario(tipoEnum);
        usuario.setTelefone(cadastro.getTelefone());
        usuario.setDataDeNascimento(cadastro.getDataNascimento());
        Endereco endereco = createEndereco(cadastro);
        usuario.getEnderecos().add(endereco);
        endereco.setUsuario(usuario);
        cadastroTemporarioRepository.delete(cadastro);

        return userRepo.save(usuario);
    }

    private static @NonNull Endereco createEndereco(CadastroTemporario cadastro) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(cadastro.getEnderecoTemporario().getLogradouro());
        endereco.setNumero(cadastro.getEnderecoTemporario().getNumero());
        endereco.setComplemento(cadastro.getEnderecoTemporario().getComplemento());
        endereco.setSiglaEstado(cadastro.getEnderecoTemporario().getSiglaEstado());
        endereco.setCep(cadastro.getEnderecoTemporario().getCep());
        endereco.setLongitude(cadastro.getEnderecoTemporario().getLongitude());
        endereco.setLatitude(cadastro.getEnderecoTemporario().getLatitude());
        endereco.setEnderecoPrincipal(true);
        endereco.setNomeCidade(cadastro.getEnderecoTemporario().getNomeCidade());
        return endereco;
    }

    @Transactional
    public void cadastrarEndereco(@Valid EnderecoRequestDTO dto, Long idSeguro) {

        CadastroTemporario cadastro = cadastroTemporarioRepository.findById(idSeguro)
                .orElseThrow(() -> new EntityNotFoundException("Não encontramos o seu usuário"));
        EnderecoTemporario enderecoTemporario = new EnderecoTemporario();
        enderecoTemporario.setLogradouro(dto.logradouro());
        enderecoTemporario.setNumero(dto.numero());
        enderecoTemporario.setComplemento(dto.complemento());
        enderecoTemporario.setSiglaEstado(dto.siglaEstado());
        enderecoTemporario.setCep(dto.cep());
        enderecoTemporario.setLongitude(dto.longitude());
        enderecoTemporario.setLatitude(dto.latitude());
        enderecoTemporario.setNomeCidade(dto.nomeCidade());
        cadastro.setEnderecoTemporario(enderecoTemporario);
        cadastroTemporarioRepository.save(cadastro);
    }
}