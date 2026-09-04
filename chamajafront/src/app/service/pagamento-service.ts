import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PagamentoRequestDTO } from '../DTOS/Pagamento/PagamentoRequestDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class PagamentoService {

  private readonly apiUrl = 'http://localhost:8080/api/pagamentos'
  private http = inject(HttpClient);

  pagar (pagamentoRequestDTO : PagamentoRequestDTO) : Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/pagar`, pagamentoRequestDTO);
  }

}
