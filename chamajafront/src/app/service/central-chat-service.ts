import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CentralGenericDTO } from '../DTOS/Chat/CentralGenericDTO.dto';
import { InteracaoInicialResponseDTO } from '../DTOS/InteracaoInicial/InteracaoInicialResponseDTO.dto';
import { MensagemChatDTO } from '../DTOS/Chat/MensagemChatDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class CentralChatService {
  private readonly api_url = 'http://localhost:8080/api/chat';
  private http = inject(HttpClient);

  obterContatosUsuario(): Observable<CentralGenericDTO[]> {
    return this.http.get<CentralGenericDTO[]>(`${this.api_url}/obter-contatos`);
  }

  obterDetalhesInteracaoInicial (id : number) : Observable<InteracaoInicialResponseDTO>{
    return this.http.get<InteracaoInicialResponseDTO>(`${this.api_url}/obter-detalhes-interacao/${id}`);
  }

  obterMensagensChamado (idChamado : number) : Observable<MensagemChatDTO []>{
    return this.http.get<MensagemChatDTO[]>(`${this.api_url}/chamado/${idChamado}/mensagens`);
  }
}
