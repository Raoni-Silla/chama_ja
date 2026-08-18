import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { InteracaoInicialRequestDTO } from '../DTOS/InteracaoInicial/InteracaoInicialRequestDTO.dto';
import { Observable } from 'rxjs';
import { InteracaoInicialResponseDTO } from '../DTOS/InteracaoInicial/InteracaoInicialResponseDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class InteracaoInicial {

  private readonly apiURL = 'http://localhost:8080/api/interacao';

  constructor (private http : HttpClient) {

  }

  criarInteracaoInicial (dto : InteracaoInicialRequestDTO) : Observable<InteracaoInicialResponseDTO> {
    return this.http.post<InteracaoInicialResponseDTO>(`${this.apiURL}/criar`, dto);
  }

  recusarInteracao (id : number) : Observable<void>{
    return this.http.patch<void>(`${this.apiURL}/recusar/${id}`,null);
  }

  comecarNegociacao(id: number) : Observable<number>{
    return this.http.patch<number>(`${this.apiURL}/comecar-negociacao/${id}`, null)
  }

}
