import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CategoriaDetalhesDTO } from '../DTOS/Categoria/CategoriaDetalhesDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class CategoriaService {


  private apiUrl = 'http://localhost:8080/api/categorias';

  constructor(private http: HttpClient) { }

  obterOitoCategorias(): Observable<CategoriaDetalhesDTO[]> {
    return this.http.get<CategoriaDetalhesDTO[]>(`${this.apiUrl}/obter-8-categorias`);
  }


}
