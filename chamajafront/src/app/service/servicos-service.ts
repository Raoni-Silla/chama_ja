import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ServicoResponseDTO } from '../DTOS/Servico/ServicoResponseDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class ServicosService {
  private readonly apiUrl = 'http://localhost:8080/api/servicos';
  private http = inject(HttpClient);

  listarServicos(): Observable<ServicoResponseDTO[]> {
    return this.http.get<ServicoResponseDTO[]>(`${this.apiUrl}/listar-servicos`);
  }

  concluirServico (idChamado : number) : Observable<void>{
    return this.http.patch<void>(`${this.apiUrl}/concluir-servico/${idChamado}`, null)
  }
}
