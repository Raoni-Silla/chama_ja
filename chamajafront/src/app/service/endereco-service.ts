import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GeoapifyResponse } from '../DTOS/GeoApi/GeoapifyResponse.dto';
import { environment } from '../../environments/environment.development';
import { EnderecoRequestDTO } from '../DTOS/Endereco/EnderecoRequestDTO.dto';
import { EnderecoResponseDTO } from '../DTOS/Endereco/EnderecoResponseDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class EnderecoService {
  private apiUrl = 'http://localhost:8080/api/enderecos';

  constructor(private http: HttpClient) {}

  buscarEndereco(texto: string): Observable<GeoapifyResponse> {
    const url = `https://api.geoapify.com/v1/geocode/autocomplete?text=${texto}&apiKey=${environment.geoapifyKey}&lang=pt&limit=5&filter=countrycode:br`;
    return this.http.get<GeoapifyResponse>(url);
  }

  buscarEnderecoPorCoordenadas(lat: number, lon: number): Observable<any> {
    const url = `https://api.geoapify.com/v1/geocode/reverse?lat=${lat}&lon=${lon}&apiKey=${environment.geoapifyKey}&lang=pt`;
    return this.http.get(url);
  }

  salvarEndereco(endereco: EnderecoRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/salvar-endereco`, endereco);
  }

  obterEnderecos(): Observable<EnderecoResponseDTO[]> {
    return this.http.get<EnderecoResponseDTO[]>(`${this.apiUrl}/obter-enderecos`);
  }

  excluirEndereco(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar-endereco/${id}`);
  }

  atualizarEndereco(id: number, dto: EnderecoRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/atualizar-endereco/${id}`, dto);
  }

  definirNovoEnderecoComoPrincipal (id : number) : Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/definir-endereco-principal/${id}`, null)
  }
}
