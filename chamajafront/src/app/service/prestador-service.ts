import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PrestadorResponseDTO } from '../DTOS/Prestador/PrestadorResponseDTO.dto';
import { MelhoresDoMesDTO } from '../DTOS/MelhoresDoMes/MelhoresDoMesDTO.dto';
import { CarregarHomePrestadorDTO } from '../DTOS/Prestador/CarregarHomePrestadorDTO.dto';
import { CarregarAreasAtuacaoPrestador } from '../DTOS/Prestador/CarregarAreasAtuacaoPrestador.dto';

@Injectable({
  providedIn: 'root',
})
export class PrestadorService {
  private apiUrl = 'http://localhost:8080/api/prestadores';

  constructor(private http: HttpClient) {}

  buscarPrestadores(termo: string): Observable<PrestadorResponseDTO[]> {
    let params = new HttpParams();
    if (termo) {
      params = params.set('q', termo);
    }
    return this.http.get<PrestadorResponseDTO[]>(`${this.apiUrl}/buscar`, { params });
  }

  buscarPrestadorPorId(id: number): Observable<PrestadorResponseDTO> {
    return this.http.get<PrestadorResponseDTO>(`${this.apiUrl}/${id}`);
  }

  obterTop5MelhoresPrestadores(): Observable<MelhoresDoMesDTO[]> {
    return this.http.get<MelhoresDoMesDTO[]>(`${this.apiUrl}/top4`);
  }

  carregarHomePrestador(): Observable<CarregarHomePrestadorDTO> {
    return this.http.get<CarregarHomePrestadorDTO>(`${this.apiUrl}/carregar-home`);
  }

  carregarCategoriasPrestador(): Observable<CarregarAreasAtuacaoPrestador> {
    return this.http.get<CarregarAreasAtuacaoPrestador>(`${this.apiUrl}/carregar-categorias`);
  }

  adicionarCategoria(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/adicionar-categoria/${id}`, null);
  }

  removerCategoria(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/remover-categoria/${id}`, null);
  }
}
