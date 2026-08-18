import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { PropostaRequestDTO } from '../DTOS/Proposta/PropostaRequestDTO.dto';
import { PropostaResponseDTO } from '../DTOS/Proposta/PropostaResponseDTO.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PropostaService {

  private http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/proposta'

  criarProposta (idChamado : number, dto : PropostaRequestDTO) : Observable<PropostaResponseDTO> {
    return this.http.post<PropostaResponseDTO>(`${this.apiUrl}/chamado/${idChamado}`,dto);
  }

  obterPropostaPendente(idChamado : number) : Observable<PropostaResponseDTO>{
    return this.http.get<PropostaResponseDTO>(`${this.apiUrl}/chamado/${idChamado}/pendente`)
  }

  recusarProposta(idProposta : number) : Observable<void>{
    return this.http.patch<void>(`${this.apiUrl}/recusar-proposta/${idProposta}`, null)
  }

   aceitarProposta(idProposta : number) : Observable<void>{
    return this.http.patch<void>(`${this.apiUrl}/aceitar-proposta/${idProposta}`, null)
  }

}
