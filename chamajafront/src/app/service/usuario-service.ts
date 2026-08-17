import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UsuarioInfoBasicasDTO } from '../DTOS/Usuario/UsuarioInfoBasica.dto';
import { UsuarioInfoPerfilDTO } from '../DTOS/Usuario/UsuarioInfoPerfilDTO.dto';
import { UsuarioTrocaSenhaDTO } from '../DTOS/Usuario/UsuarioTrocaSenhaDTO.dto';
import { EnderecoResponseDTO } from '../DTOS/Endereco/EnderecoResponseDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class UsuarioService {
  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient) {}

  obterInfosBasicasUsuarioLogado(): Observable<UsuarioInfoBasicasDTO> {
    return this.http.get<UsuarioInfoBasicasDTO>(`${this.apiUrl}/obter-infos-basicas`);
  }

  obterInfosParaTelaDePerfil(): Observable<UsuarioInfoPerfilDTO> {
    return this.http.get<UsuarioInfoPerfilDTO>(`${this.apiUrl}/obter-infos-perfil`);
  }

  salvarInfosModificadasDaTelaDePerfil(
    dto: UsuarioInfoPerfilDTO,
  ): Observable<UsuarioInfoPerfilDTO> {
    dto.email = dto.email.trim();
    dto.nome = dto.nome.trim();
    return this.http.post<UsuarioInfoPerfilDTO>(`${this.apiUrl}/salvar-modificacoes`, dto);
  }

  enviarCodigoSms(telefone: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/mudar-telefone/${telefone}`, null);
  }

  validarCodigoSms(codigo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/confirmar-codigo/${codigo}`, null);
  }

  trocarSenha(senhaAntiga: string, senhaNova: string): Observable<void> {
    const dto: UsuarioTrocaSenhaDTO = {
      senhaAntiga: senhaAntiga,
      senhaNova: senhaNova,
    };
    return this.http.post<void>(`${this.apiUrl}/mudar-senha`, dto);
  }

  obterCpf(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/obter-cpf`);
  }

  desativarConta(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/desativar`, null);
  }

  excluirConta(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/excluir`);
  }

  obterTodosEnderecosUsuarioLogado(): Observable<EnderecoResponseDTO[]> {
    return this.http.get<EnderecoResponseDTO[]>(`${this.apiUrl}/obter-enderecos`);
  }
}
