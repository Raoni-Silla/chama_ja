import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CadastroRequestDTO } from '../DTOS/Cadastro/CadastroRequestDTO.dto';
import { Observable } from 'rxjs';
import { CadastroResponseDTO } from '../DTOS/Cadastro/CadastroResponseDTO.dto';
import { UsuarioResponseDTO } from '../DTOS/Usuario/UsuarioResponseDTO.dto';
import { LoginResponseDTO } from '../DTOS/Login/LoginResponseDTO.dto';
import { LoginRequestDTO } from '../DTOS/Login/LoginRequestDTO.dto';
import { EnderecoRequestDTO } from '../DTOS/Endereco/EnderecoRequestDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class LoginService {

  apiCadastro = 'http://localhost:8080/registro';
  apiLogin = 'http://localhost:8080/login';;

  private tokenCadastro: string | null = null;

  constructor(private http: HttpClient) { }

  salvarToken(token: string) {
    this.tokenCadastro = token;
    sessionStorage.setItem('token_cadastro_chamaja', token);
  }

  obterToken(): string | null {
    if (!this.tokenCadastro) {
      this.tokenCadastro = sessionStorage.getItem('token_cadastro_chamaja');
    }
    return this.tokenCadastro;
  }

  limparToken() {
    this.tokenCadastro = null;
    sessionStorage.removeItem('token_cadastro_chamaja');
  }

  iniciarCadastro(dto: CadastroRequestDTO): Observable<CadastroResponseDTO> {
    return this.http.post<CadastroResponseDTO>(`${this.apiCadastro}/iniciar`, dto);
  }

  adicionarTelefone(telefone: string): Observable<CadastroResponseDTO> {
    return this.http.post<CadastroResponseDTO>(
      `${this.apiCadastro}/telefone`,
      null,
      {
        params: { telefone: telefone }
      }
    );
  }

  solicitarEnvioSMS(): Observable<CadastroResponseDTO> {
    return this.http.post<CadastroResponseDTO>(
      `${this.apiCadastro}/telefone/solicitar-envio-sms`,
      null
    );
  }

  confirmarCodigoSMS(codigo: string): Observable<CadastroResponseDTO> {
    return this.http.post<CadastroResponseDTO>(
      `${this.apiCadastro}/telefone/confirmar-codigo-sms`,
      null,
      {
        params: { codigo: codigo }
      }
    );
  }

  tipoUsuario(tipoUsuario: string): Observable<UsuarioResponseDTO> {
    return this.http.post<UsuarioResponseDTO>(
      `${this.apiCadastro}/tipo-usuario`,
      null,
      {
        params: { tipoUsuario: tipoUsuario }
      }
    );
  }

  autenticar(email: string, senha: string): Observable<LoginResponseDTO> {
    const request: LoginRequestDTO = { email, senha };
    return this.http.post<LoginResponseDTO>(`${this.apiLogin}`, request);
  }


   salvarEnderecoCadastroTemporario (dto : EnderecoRequestDTO) : Observable<void> {
    return this.http.post<void>(`${this.apiCadastro}/confirmar-endereco`,dto)
   }
}